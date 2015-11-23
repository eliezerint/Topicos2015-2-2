package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by eli on 05/10/2015.
 */
public class Passaro {

    private final World mundo;
    private final OrthographicCamera camera;
    private final Texture[] texturas;
    private Body corpo;
    private Sprite sprite;

    public Passaro(World mundo , OrthographicCamera camera, Texture[] texturas){

        this.mundo = mundo;
        this.camera = camera;
        this.texturas = texturas;
        this.sprite = new Sprite(texturas[0]);
        initCorpo();
    }

    private void initCorpo(){
        float x = (camera.viewportWidth / 2) / Util.PIXEL_METRO;
        float y = (camera.viewportHeight / 2) / Util.PIXEL_METRO;

        corpo = Util.criarCorpo(mundo, BodyDef.BodyType.DynamicBody, x, y);

        FixtureDef definicao = new FixtureDef();
        definicao.density = 1;
        definicao.friction = 0.4f;
        definicao.restitution = 0.3f;

        BodyEditorLoader loader =
                new BodyEditorLoader(Gdx.files.internal("physics/bird.json"));

         loader.attachFixture(corpo, "bird", definicao, 1, "PASSARO");
    }

    /**
     * Atualiza o comportamento do passaro
     * @param delta
     */

    public  void atualiazar(float delta, boolean movimentar){
        if (movimentar)
        atualializarVelocidade();
    }

    private void atualializarVelocidade() {
        corpo.setLinearVelocity(2, corpo.getLinearVelocity().y);
    }

    /**
     * Aplica uma força positiva no Y para simular o Pulo
     */
    public void pular(){
        corpo.setLinearVelocity(corpo.getLinearVelocity().x, 0);
        corpo.applyForceToCenter(0, 100, false);
    }

    public Body getCorpo(){
        return corpo;
    }

    public void renderizar(SpriteBatch pincel){
        Vector2 posica = corpo.getPosition();
        sprite.setTexture(texturas[0]);
        sprite.setPosition(posica.x * Util.PIXEL_METRO, posica.y * Util.PIXEL_METRO);
        sprite.setOrigin(0, 0);
        sprite.setRotation((float) Math.toDegrees(corpo.getAngle()));
        sprite.draw(pincel);


    }


}
