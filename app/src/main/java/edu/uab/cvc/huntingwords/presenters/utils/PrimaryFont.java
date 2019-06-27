package edu.uab.cvc.huntingwords.presenters.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PrimaryFont {
    private static PrimaryFont instance = null;
    private FreeTypeFontGenerator generator;

    private PrimaryFont() {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSansFallback.ttf"));
    }

    public static PrimaryFont getInstance() {
        if (instance == null) {
            instance = new PrimaryFont();
        }

        return instance;
    }

    private BitmapFont getFont(int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameters.size = size;
        parameters.borderWidth = 1;
        parameters.color = Color.WHITE;
        parameters.characters =  " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~主菜單繼續觸碰屏幕可返回主菜單難度分數生命值密碼用戶名登錄註冊退出登錄教學開始加載中主菜单继续触碰屏幕可返回主菜单难度分数生命值密码用户名登录注册退出登录教学开始加载中玩家";
        return generator.generateFont(parameters);
    }

    public Skin getSkin(int size) {
        Skin skin = new Skin();
        skin.addRegions(new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas")));
        skin.add("default-font",getFont(size), BitmapFont.class);
        skin.load(Gdx.files.internal("skin/uiskin.json"));
        return skin;
    }
}
