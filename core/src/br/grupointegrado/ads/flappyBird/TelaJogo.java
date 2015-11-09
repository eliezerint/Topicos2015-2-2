package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;


/**
 * Created by eli on 28/09/2015.
 */
public class TelaJogo extends TelaBase {



    private OrthographicCamera camera;// camera do jogo
    private World mundo; // representa o mundo na tela do box2D
    private Body chao; // corpo do chao
    private Passaro passaro;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    private  int potuacao = 0;
    private BitmapFont fontepontuacao;
    private Stage  palcoInformacao;
    private Label lbPontucao;
    private ImageButton btnplay;
    private ImageButton btngameOver;
    private OrthographicCamera camerainf;

    private Box2DDebugRenderer debug; // desenha o mundo na tela para ajudar no desenvolvimento;




    public TelaJogo (MainGame game){
        super(game);
    }


    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth()/ Util.ESCALA, Gdx.graphics.getHeight() / Util.ESCALA);
        debug = new Box2DDebugRenderer();
        mundo = new World(new Vector2(0, -9.8f),false);
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                detectarColisao(contact.getFixtureA(), contact.getFixtureB());
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        initChao();
        initPassaro();
        initFontes();
        initInformacao();



    }

    private boolean gameOver = false;

    /**
     *  Verifica se o passaro esta envolvido na colisão
     *
     * @param fixtureA
     * @param fixtureB
     */

    private void detectarColisao(Fixture fixtureA, Fixture fixtureB) {
       if ("PASSARO".equals(fixtureA.getUserData())||
               "PASSARO".equals(fixtureB.getUserData())){
           gameOver = true;

           //game over
       }


    }

    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter fonteParam =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        fonteParam.size = 56;
        fonteParam.color = Color.WHITE;
        fonteParam.shadowColor = Color.BLACK;
        fonteParam.shadowOffsetX = 4;
        fonteParam.shadowOffsetY = 4;

         FreeTypeFontGenerator gerador =
                 new  FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
    }

    private void initInformacao() {
       palcoInformacao = new Stage(new FillViewport(camerainf.viewportWidth,
               camerainf.viewportHeight, camerainf));

        Gdx.input.setInputProcessor(palcoInformacao);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fontepontuacao;

        lbPontucao = new Label("0", estilo);
        palcoInformacao.addActor(lbPontucao);


    }

    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0 ,0);




    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, null);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1); // limpa a tela e pinta a cor de fundo;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);// matem o buffer de cores;

        capturaTeclas();

        atualizar(delta);

        renderizar(delta);

        debug.render(mundo, camera.combined.cpy().scl(Util.PIXEL_METRO));


    }

    private boolean pulando = false;

    private void capturaTeclas() {
        pulando = false;
        if (Gdx.input.justTouched()){
            pulando = true;
        }
    }

    /**
     * Redenrizar/desenhar as imagens
     *
     *
     * @param delta
     */

    private void renderizar(float delta) {
        palcoInformacao.draw();

    }

    /**
     * Atualização e cálculo dos corpos
     *
     * @param delta
     */


    private void atualizar(float delta) {
        palcoInformacao.act(delta);

        passaro.atualiazar(delta);
        mundo.step(1f / 60f, 6, 2);

        atualizaInformacao();
        atualizarObstaculos();
        atualizarCamera();
        atualizarChao();
        if (pulando){
            passaro.pular();
        }
    }

    private void atualizaInformacao() {
        lbPontucao.setText(potuacao+"");
        lbPontucao.setPosition(
                camerainf.viewportWidth / 2 - lbPontucao.getPrefWidth()/ 2,
                camerainf.viewportHeight - lbPontucao.getPrefHeight());
    }

    private void atualizarObstaculos() {

        // Enquantoi a lista tiver menos do que 4, cria obstaculos
        while (obstaculos.size < 4){
            Obstaculo ultimo = null;
            if (obstaculos.size > 0) {
                ultimo = obstaculos.peek();// recupere o ultimo item da lista
                Obstaculo o = new Obstaculo(mundo, camera, ultimo);
                obstaculos.add(o);
            }
            // verifica se os obtaculos sairam  da tela para remover

            for (Obstaculo o : obstaculos){
                float inicioCamera = passaro.getCorpo().getPosition().x -
                        (camera.viewportWidth /2 / Util.PIXEL_METRO) - o.getLargura();
                if (inicioCamera > o.getPosX()){
                    o.remover();
                    obstaculos.removeValue(o,true);
                }else if(!o.isPassou() && o.getPosX() < passaro.getCorpo().getPosition().x){
                  o.setPassou(true);
                    //Calcula pontuação
                    potuacao++;
                    //reproduzir o som
                }

            }
        }
    }

    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x - 32 / Util.PIXEL_METRO ) * Util.PIXEL_METRO;
        camera.update();
    }

    /**
     * Atualiza a posição do chão para companhar o  passaro
     */

    private void atualizarChao() {
        Vector2 posicao = passaro.getCorpo().getPosition(); // Pega posição do passaro.

        chao.setTransform(posicao.x, 0, 0); // Faz o chão acompanhar o passaro.


    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Util.ESCALA , height / Util.ESCALA);
        camera.update();

        redimensionaChao();
        camerainf.setToOrtho(false, width, height);
        camerainf.update();
    }

    /**
     * configura o tamanho do chão de acordo com o tamanho da tela;
     */

    private void redimensionaChao() {
        chao.getFixtureList().clear();
        float largura = camera.viewportWidth / Util.PIXEL_METRO;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, Util.ALTURA_CHAO / 2);

        Fixture forma = Util.criarForma(chao, shape, "Chao");
        shape.dispose();



    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        debug.dispose();
        mundo.dispose();
        palcoInformacao.dispose();
        fontepontuacao.dispose();

    }
}
