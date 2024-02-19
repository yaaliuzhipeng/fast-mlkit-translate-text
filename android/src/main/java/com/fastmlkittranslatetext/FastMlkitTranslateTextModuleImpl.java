package com.fastmlkittranslatetext;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.IdentifiedLanguage;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.List;
import java.util.Set;

public class FastMlkitTranslateTextModuleImpl {

  public static final String NAME = "FastMlkitTranslateText";
  public static String CODE_IDENTIFY_FAIL = "-1000";
  public static String CODE_IDENTIFY_FAIL_UND = "-1001";
  public static String CODE_CHECK_DOWNLOAD_FAIL = "-2000";
  public static String CODE_DOWNLOAD_FAIL = "-2001";
  public static String CODE_DOWNLOADED_MODELS_FAIL = "-2002";
  public static String CODE_LANG_NULL = "-3000";
  public static String CODE_LANG_TRANSLATE_FAIL = "-3001";

  private LanguageIdentifier identifier;
  private Translator translator;

  private boolean downloadModelIfNeeded = false;

  private void ensureIdentifierInitialized() {
    if (identifier == null) {
      identifier = LanguageIdentification.getClient();
    }
  }

  public void prepare(ReadableMap options, Promise promise) {
    String source = options.getString("source");
    String target = options.getString("target");
    if (options.hasKey("downloadIfNeeded")) {
      downloadModelIfNeeded = options.getBoolean("downloadIfNeeded");
    }
    if (source != null && target != null) {
      String sourceLang = TranslateLanguage.fromLanguageTag(source);
      String targetLang = TranslateLanguage.fromLanguageTag(target);
      if (sourceLang != null && targetLang != null) {
        if (translator != null) {
          translator.close();
          translator = null;
        }
        TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
          .setSourceLanguage(sourceLang)
          .setTargetLanguage(targetLang)
          .build();
        translator = Translation.getClient(translatorOptions);
        promise.resolve(true);
        return;
      }
    }
    promise.reject(
      CODE_LANG_NULL,
      "source or target language not supported,or you passed wrong language tag"
    );
  }

  private void doTranslate(String text, Promise promise) {
    translator
      .translate(text)
      .addOnSuccessListener(
        new OnSuccessListener<String>() {
          @Override
          public void onSuccess(String s) {
            promise.resolve(s);
          }
        }
      )
      .addOnFailureListener(
        new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            promise.reject(CODE_LANG_TRANSLATE_FAIL, e.getLocalizedMessage());
          }
        }
      );
  }

  public void translate(String text, Promise promise) {
    if (downloadModelIfNeeded) {
      translator
        .downloadModelIfNeeded()
        .addOnSuccessListener(
          new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
              //ready , go translate
              doTranslate(text, promise);
            }
          }
        )
        .addOnFailureListener(
          new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              promise.reject(CODE_DOWNLOAD_FAIL, e.getLocalizedMessage());
            }
          }
        );
    } else {
      doTranslate(text, promise);
    }
  }

  public void getDownloadedLanguageModels(Promise promise) {
    RemoteModelManager manager = RemoteModelManager.getInstance();
    manager
      .getDownloadedModels(TranslateRemoteModel.class)
      .addOnSuccessListener(
        new OnSuccessListener<Set<TranslateRemoteModel>>() {
          @Override
          public void onSuccess(
            Set<TranslateRemoteModel> translateRemoteModels
          ) {
            WritableArray array = Arguments.createArray();
            for (TranslateRemoteModel model : translateRemoteModels) {
              array.pushString(model.getLanguage());
            }
            promise.resolve(array);
          }
        }
      )
      .addOnFailureListener(
        new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            promise.reject(
              CODE_DOWNLOADED_MODELS_FAIL,
              e.getLocalizedMessage()
            );
          }
        }
      );
  }

  public void deleteLanguageModel(String lang, Promise promise) {
    RemoteModelManager manager = RemoteModelManager.getInstance();
    TranslateRemoteModel trm = new TranslateRemoteModel.Builder(lang).build();
    manager
      .isModelDownloaded(trm)
      .addOnSuccessListener(
        new OnSuccessListener<Boolean>() {
          @Override
          public void onSuccess(Boolean downloaded) {
            if (downloaded) {
              manager.deleteDownloadedModel(trm);
            }
            promise.resolve(true);
          }
        }
      )
      .addOnFailureListener(
        new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            promise.reject(CODE_CHECK_DOWNLOAD_FAIL, e.getLocalizedMessage());
          }
        }
      );
  }

  public void isLanguageDownloaded(String lang, Promise promise) {
    RemoteModelManager manager = RemoteModelManager.getInstance();
    TranslateRemoteModel trm = new TranslateRemoteModel.Builder(lang).build();
    manager
      .isModelDownloaded(trm)
      .addOnSuccessListener(
        new OnSuccessListener<Boolean>() {
          @Override
          public void onSuccess(Boolean downloaded) {
            promise.resolve(downloaded);
          }
        }
      )
      .addOnFailureListener(
        new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            promise.reject(CODE_CHECK_DOWNLOAD_FAIL, e.getLocalizedMessage());
          }
        }
      );
  }

  public void downloadLanguageModel(String lang, Promise promise) {
    RemoteModelManager manager = RemoteModelManager.getInstance();
    TranslateRemoteModel trm = new TranslateRemoteModel.Builder(lang).build();
    manager
      .isModelDownloaded(trm)
      .addOnSuccessListener(
        new OnSuccessListener<Boolean>() {
          @Override
          public void onSuccess(Boolean downloaded) {
            if (downloaded) {
              promise.resolve(true);
            } else {
              //download model
              manager
                .isModelDownloaded(trm)
                .addOnSuccessListener(
                  new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean downloaded) {
                      if (!downloaded) {
                        manager
                          .download(
                            trm,
                            new DownloadConditions.Builder().build()
                          )
                          .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {
                                promise.resolve(true);
                              }
                            }
                          )
                          .addOnFailureListener(
                            new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                promise.reject(
                                  CODE_DOWNLOAD_FAIL,
                                  e.getLocalizedMessage()
                                );
                              }
                            }
                          );
                      }
                    }
                  }
                )
                .addOnFailureListener(
                  new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      promise.reject(
                        CODE_CHECK_DOWNLOAD_FAIL,
                        e.getLocalizedMessage()
                      );
                    }
                  }
                );
            }
          }
        }
      )
      .addOnFailureListener(
        new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            promise.reject(CODE_CHECK_DOWNLOAD_FAIL, e.getLocalizedMessage());
          }
        }
      );
  }

  public void setIdentifyConfidence(double confidence) {
    identifier =
      LanguageIdentification.getClient(
        new LanguageIdentificationOptions.Builder()
          .setConfidenceThreshold((float) confidence)
          .build()
      );
  }

  public void identify(String text, Promise promise) {
    ensureIdentifierInitialized();
    identifier
      .identifyLanguage(text)
      .addOnSuccessListener(
        new OnSuccessListener<String>() {
          @Override
          public void onSuccess(String s) {
            if (s.equals("und")) {
              promise.reject(CODE_IDENTIFY_FAIL_UND, s);
            } else {
              promise.resolve(s);
            }
          }
        }
      )
      .addOnFailureListener(
        new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            promise.reject(CODE_IDENTIFY_FAIL, e.getLocalizedMessage());
          }
        }
      );
  }

  public void identifyPossible(String text, Promise promise) {
    identifier
      .identifyPossibleLanguages(text)
      .addOnSuccessListener(
        new OnSuccessListener<List<IdentifiedLanguage>>() {
          @Override
          public void onSuccess(List<IdentifiedLanguage> identifiedLanguages) {
            WritableArray array = Arguments.createArray();
            for (IdentifiedLanguage lang : identifiedLanguages) {
              WritableMap map = Arguments.createMap();
              map.putString("lang", lang.getLanguageTag());
              map.putDouble("confidence", lang.getConfidence());
              array.pushMap(map);
            }
            promise.resolve(array);
          }
        }
      )
      .addOnFailureListener(
        new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            promise.reject(CODE_IDENTIFY_FAIL, e.getLocalizedMessage());
          }
        }
      );
  }
}
