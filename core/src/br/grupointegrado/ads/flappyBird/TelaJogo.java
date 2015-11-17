package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;




/**
 * Created by eli on 28/09/2015.
 */
public class TelaJogo extends TelaBase {



    private OrthographicCamera camera; // camera do jogo
    private World mundo; // representa o mundo do Box2D
    private Body chao; // corpo do chao
    private Passaro passaro;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    private int pontuacao = 0;
    private BitmapFont fontePontuacao;
    private Stage palcoInformacoes;
    private Label lbPontuacao;
    private ImageButton btnPlay;
    private ImageButton btnGameOver;
    private OrthographicCamera cameraInfo;
    private Texture[] texturasPassaros;
    private Texture texturasObstaculosAcima;
    private Texture texturasObstaculosBaixo;
    private Texture texturasChao;
    private Texture texturasFundo;
    private Texture texturasPlay;
    private Texture texturasGameover;


    private Box2DDebugRenderer debug; // desenha o mundo na tela para ajudar no desenvolvimento
    private Boolean jogadorIniciado = false;

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / Util.ESCALA, Gdx.graphics.getHeight() / Util.ESCALA);
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debug = new Box2DDebugRenderer();
        mundo = new World(new Vector2(0,-9.8f), false);
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
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        initTexturas();
        initChao();
        initPassaro();
        initFontes();
        initInformacoes();

    }

    private void initTexturas() {
        texturasPassaros = new Texture[3];
        texturasPassaros[0] = new Texture("sprites/bird-1.png");
        texturasPassaros[1] = new Texture("sprites/bird-2.png");
        texturasPassaros[2] = new Texture("sprites/bird-3.png");

        texturasObstaculosAcima = new Texture("sprites/toptube.png");
        texturasObstaculosBaixo = new Texture("sprites/bottomtube.png");

        texturasFundo = new Texture("sprites/bg.png");
        texturasChao = new Texture("sprites/ground.png");
        texturasPlay = new Texture("sprites/playbtn.png");

        texturasGameover = new Texture("sprites/gameover.png");




    }

    private boolean gameOver = false;

    /**
     * Verifica se o passaro esta envolvido na colisao
     * @param fixtureA
     * @param fixtureB
     */

    private void detectarColisao(Fixture fixtureA, Fixture fixtureB) {
        if ("PASSARO".equals(fixtureA.getUserData()) || "PASSARO".equals(fixtureB.getUserData())) {
            // game over
            gameOver = true;
        }
    }

    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter fonteParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fonteParam.size = 56;
        fonteParam.color = Color.WHITE;
        fonteParam.shadowColor = Color.BLACK;
        fonteParam.shadowOffsetX = 4;
        fonteParam.shadowOffsetY = 4;

        FreeTypeFontGenerator gerador = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        fontePontuacao = gerador.generateFont(fonteParam);

        gerador.dispose();
    }

    private void initInformacoes() {
        // inicia label
        palcoInformacoes = new Stage(new FillViewport(cameraInfo.viewportWidth, cameraInfo.viewportHeight, cameraInfo));
        Gdx.input.setInputProcessor(palcoInformacoes);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fontePontuacao;

        lbPontuacao = new Label("0", estilo);
        palcoInformacoes.addActor(lbPontuacao);

        //inicia botão
        ImageButton.ImageButtonStyle estiloBotao = new ImageButton.ImageButtonStyle();
        estiloBotao.up = new SpriteDrawable(new Sprite(texturasPlay));
        btnPlay = new ImageButton(estiloBotao);
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                jogadorIniciado = true;
            }
        });
        palcoInformacoes.addActor(btnPlay);

        estiloBotao = new ImageButton.ImageButtonStyle();
        estiloBotao.up = new SpriteDrawable(new Sprite(texturasGameover));

        btnGameOver = new ImageButton(estiloBotao);
        btnGameOver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reiniciarJogo();
            }
        });

        palcoInformacoes.addActor(btnGameOver);
    }

    private void reiniciarJogo() {
        game.setScreen(new TelaJogo(game));

    }


    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0, 0);
    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1); // limpa a tela e pinta a cor de fundo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // mantem o buffer de cores

        capturaTeclas();

        atualizar(delta);
        renderizar(delta);

        debug.render(mundo, camera.combined.cpy().scl(Util.PIXEL_METRO));
    }

    private boolean pulando = false;

    private void capturaTeclas() {
        pulando = false;
        if (Gdx.input.justTouched()) {
            pulando = true;
        }
    }

    /**
     * Renderizar/desenhar as imagens
     * @param delta
     */

    private void renderizar(float delta) {
        palcoInformacoes.draw();
    }

    /**
     * Atualizacao e calculo dos corpos
     * @param delta
     */
    private void atualizar(float delta) {
        palcoInformacoes.act(delta);
        passaro.atualiazar(delta, !gameOver);
        if(jogadorIniciado){
            mundo.step(1f / 60f, 6, 2);
            atualizarObstaculos();
        }
        atualizarInformacoes();

        if(!gameOver){
            atualizarCamera();
            atualizarChao();
        }
        if((pulando) && (!gameOver) && (jogadorIniciado)){
            passaro.pular();
        }
    }

    private void atualizarInformacoes() {
        lbPontuacao.setText(pontuacao + "");
        lbPontuacao.setPosition(cameraInfo.viewportWidth / 2 - lbPontuacao.getPrefWidth() / 2, cameraInfo.viewportHeight - lbPontuacao.getPrefHeight());
        btnPlay.setPosition(cameraInfo.viewportWidth / 2 - btnPlay.getPrefWidth() / 2, cameraInfo.viewportHeight / 2 - btnPlay.getPrefHeight() * 2);
        btnPlay.setVisible(!jogadorIniciado);

        btnGameOver.setPosition(cameraInfo.viewportWidth / 2 - btnGameOver.getPrefWidth() / 2, cameraInfo.viewportHeight / 2 - btnGameOver.getPrefHeight());
        btnGameOver.setVisible(gameOver);

    }

    private void atualizarObstaculos() {
        while(obstaculos.size <4){
            Obstaculo ultimo = null;
            if(obstaculos.size > 0){
                ultimo = obstaculos.peek();
            }
            Obstaculo o = new Obstaculo(mundo, camera, ultimo);
            obstaculos.add(o);
        }
        for(Obstaculo o : obstaculos){
            float iniciocamera = passaro.getCorpo().getPosition().x -
                    (camera.viewportWidth / 2 / Util.PIXEL_METRO) - o.getLargura();
            if(iniciocamera > o.getPosX()){
                obstaculos.removeValue(o, true);
            } else if(!o.isPassou() && o.getPosX() < passaro.getCorpo().getPosition().x){
                o.setPassou(true);
                pontuacao++;
            }
        }


    }

    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x + 34 / Util.PIXEL_METRO) * Util.PIXEL_METRO;
        camera.update();
    }

    /**
     * Atualiza a posicao do chao para acompanhar o passaro
     */

    private void atualizarChao() {
        Vector2 posicao = passaro.getCorpo().getPosition();

        chao.setTransform(posicao.x, 0, 0);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Util.ESCALA, height / Util.ESCALA);
        camera.update();
        redimensionaChao();
        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();
    }

    /**
     * Configura o tamanho do chao de acordo com o tamanho da tela.
     */
    private void redimensionaChao() {
        chao.getFixtureList().clear();
        float largura = camera.viewportWidth / Util.PIXEL_METRO;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, Util.ALTURA_CHAO / 2);
        Fixture forma = Util.criarForma(chao, shape, "CHAO");
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
        palcoInformacoes.dispose();
        fontePontuacao.dispose();
        texturasPassaros[0].dispose();
        texturasPassaros[1].dispose();
        texturasPassaros[2].dispose();

        texturasObstaculosAcima.dispose();
        texturasObstaculosBaixo.dispose();

        texturasFundo.dispose();
        texturasChao.dispose();
        texturasPlay.dispose();

        texturasGameover.dispose();

    }
}
