package edu.uab.cvc.huntingwords.presenters.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;
import java.util.Locale;

import edu.uab.cvc.huntingwords.screens.fragments.JumpGame;

public class LanguageManager {
    private static LanguageManager _instance = null;

    private static final String LANGUAGES_FILE = "languages.xml";
    private static final String DEFAULT_LANGUAGE_UK = "en_UK";
    private static final String DEFAULT_LANGUAGE_ES = "es_ES";
    private static final String DEFAULT_LANGUAGE_CA = "es_CA";
    private static final String DEFAULT_LANGUAGE_CN = "zh_CN";
    private static final String DEFAULT_LANGUAGE_TW = "zh_TW";

    private HashMap<String, String> _language;

    private LanguageManager() {
        _language = new HashMap<>();

        PlatformResolver resolver = JumpGame.getResolver();
        String _languageName = null;
        if (resolver != null) {
            _languageName = resolver.getDefaultLanguage();
        }

        System.out.println("locale language: "+Locale.getDefault().toString());

        if (!loadLanguage(_languageName)) {
            if (Locale.getDefault().toString().equals("es_ES")) {
                loadLanguage(DEFAULT_LANGUAGE_ES);
            }else if (Locale.getDefault().toString().equals("en_US")) {
                loadLanguage(DEFAULT_LANGUAGE_UK);
            }else if (Locale.getDefault().toString().equals("ca_ES")) {
                loadLanguage(DEFAULT_LANGUAGE_CA);
            }else if (Locale.getDefault().toString().equals("zh_CN")) {
                loadLanguage(DEFAULT_LANGUAGE_CN);
            }else if (Locale.getDefault().toString().equals("zh_TW_#Hant")) {
                loadLanguage(DEFAULT_LANGUAGE_TW);
            } else {
                loadLanguage(DEFAULT_LANGUAGE_UK);
            }
        }
    }

    public static LanguageManager getInstance() {
        if (_instance == null) {
            _instance = new LanguageManager();
        }

        return (_instance);
    }

    public String getString(String key) {
        String string;

        if (_language != null) {
            string = _language.get(key);

            if (string != null) {
                return string;
            }
        }

        return key;
    }


    private boolean loadLanguage(String languageName) {
        try {
            XmlReader reader = new XmlReader();
            XmlReader.Element root = reader.parse(Gdx.files.internal(LANGUAGES_FILE).reader("UTF-8"));

            Array<XmlReader.Element> languages = root.getChildrenByName("language");

            for (int i = 0; i < languages.size; ++i) {
                XmlReader.Element language = languages.get(i);

                if (language.getAttribute("name").equals(languageName)) {
                    _language.clear();
                    Array<XmlReader.Element> strings = language.getChildrenByName("string");

                    for (int j = 0; j < strings.size; ++j) {
                        XmlReader.Element string = strings.get(j);
                        String key = string.getAttribute("key");
                        String value = string.getAttribute("value");
                        value = value.replace("&lt;br /&gt;&lt;br /&gt;", "\n");
                        _language.put(key, value);
                    }

                    return true;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error loading languages file " + LANGUAGES_FILE);
            return false;
        }

        return false;
    }
}
