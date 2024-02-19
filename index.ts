import { NativeModules, Platform } from 'react-native';

const isAndroid = Platform.OS === 'android';
const LINKING_ERROR =
    `The package 'fast-mlkit-translate-text' doesn't seem to be linked. Make sure: \n\n` +
    Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
    '- You rebuilt the app after installing the package\n' +
    '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const FastMlkitTranslateTextModule = isTurboModuleEnabled
    ? require('./src/NativeFastMlkitTranslateText').default
    : NativeModules.FastMlkitTranslateText;

const NativeMLKitTranslator = FastMlkitTranslateTextModule
    ? FastMlkitTranslateTextModule
    : new Proxy(
        {},
        {
            get() {
                throw new Error(LINKING_ERROR);
            },
        }
    );


const LanguageTags: any = {
    'Afrikaans': 'af',
    'Arabic': 'ar',
    'Belarusian': 'be',
    'Bulgarian': 'bg',
    'Bengali': 'bn',
    'Catalan': 'ca',
    'Czech': 'cs',
    'Welsh': 'cy',
    'Danish': 'da',
    'German': 'de',
    'Greek': 'el',
    'English': 'en',
    'Esperanto': 'eo',
    'Spanish': 'es',
    'Estonian': 'et',
    'Persian': 'fa',
    'Finnish': 'fi',
    'French': 'fr',
    'Irish': 'ga',
    'Galician': 'gl',
    'Gujarati': 'gu',
    'Hebrew': 'he',
    'Hindi': 'hi',
    'Croatian': 'hr',
    'Haitian': 'ht',
    'Hungarian': 'hu',
    'Indonesian': 'id',
    'Icelandic': 'is',
    'Italian': 'it',
    'Japanese': 'ja',
    'Georgian': 'ka',
    'Kannada': 'kn',
    'Korean': 'ko',
    'Lithuanian': 'lt',
    'Latvian': 'lv',
    'Macedonian': 'mk',
    'Marathi': 'mr',
    'Malay': 'ms',
    'Maltese': 'mt',
    'Dutch': 'nl',
    'Norwegian': 'no',
    'Polish': 'pl',
    'Portuguese': 'pt',
    'Romanian': 'ro',
    'Russian': 'ru',
    'Slovak': 'sk',
    'Slovenian': 'sl',
    'Albanian': 'sq',
    'Swedish': 'sv',
    'Swahili': 'sw',
    'Tamil': 'ta',
    'Telugu': 'te',
    'Thai': 'th',
    'Tagalog': 'tl',
    'Turkish': 'tr',
    'Ukrainian': 'uk',
    'Urdu': 'ur',
    'Vietnamese': 'vi',
    'Chinese': 'zh',
}
const TagLanguages: any = {
    'af': 'Afrikaans',
    'ar': 'Arabic',
    'be': 'Belarusian',
    'bg': 'Bulgarian',
    'bn': 'Bengali',
    'ca': 'Catalan',
    'cs': 'Czech',
    'cy': 'Welsh',
    'da': 'Danish',
    'de': 'German',
    'el': 'Greek',
    'en': 'English',
    'eo': 'Esperanto',
    'es': 'Spanish',
    'et': 'Estonian',
    'fa': 'Persian',
    'fi': 'Finnish',
    'fr': 'French',
    'ga': 'Irish',
    'gl': 'Galician',
    'gu': 'Gujarati',
    'he': 'Hebrew',
    'hi': 'Hindi',
    'hr': 'Croatian',
    'ht': 'Haitian',
    'hu': 'Hungarian',
    'id': 'Indonesian',
    'is': 'Icelandic',
    'it': 'Italian',
    'ja': 'Japanese',
    'ka': 'Georgian',
    'kn': 'Kannada',
    'ko': 'Korean',
    'lt': 'Lithuanian',
    'lv': 'Latvian',
    'mk': 'Macedonian',
    'mr': 'Marathi',
    'ms': 'Malay',
    'mt': 'Maltese',
    'nl': 'Dutch',
    'no': 'Norwegian',
    'pl': 'Polish',
    'pt': 'Portuguese',
    'ro': 'Romanian',
    'ru': 'Russian',
    'sk': 'Slovak',
    'sl': 'Slovenian',
    'sq': 'Albanian',
    'sv': 'Swedish',
    'sw': 'Swahili',
    'ta': 'Tamil',
    'te': 'Telugu',
    'th': 'Thai',
    'tl': 'Tagalog',
    'tr': 'Turkish',
    'uk': 'Ukrainian',
    'ur': 'Urdu',
    'vi': 'Vietnamese',
    'zh': 'Chinese',
}
export type Languages = 'Afrikaans' | 'Arabic' | 'Belarusian' | 'Bulgarian' | 'Bengali' | 'Catalan' | 'Czech' | 'Welsh' | 'Danish' | 'German' | 'Greek' | 'English' | 'Esperanto' | 'Spanish' | 'Estonian' | 'Persian' | 'Finnish' | 'French' | 'Irish' | 'Galician' | 'Gujarati' | 'Hebrew' | 'Hindi' | 'Croatian' | 'Haitian' | 'Hungarian' | 'Indonesian' | 'Icelandic' | 'Italian' | 'Japanese' | 'Georgian' | 'Kannada' | 'Korean' | 'Lithuanian' | 'Latvian' | 'Macedonian' | 'Marathi' | 'Malay' | 'Maltese' | 'Dutch' | 'Norwegian' | 'Polish' | 'Portuguese' | 'Romanian' | 'Russian' | 'Slovak' | 'Slovenian' | 'Albanian' | 'Swedish' | 'Swahili' | 'Tamil' | 'Telugu' | 'Thai' | 'Tagalog' | 'Turkish' | 'Ukrainian' | 'Urdu' | 'Vietnamese' | 'Chinese';

export default class FastTranslator {

    private static getLanguageTag(lang: string): string {
        let __lang = LanguageTags[lang];
        if (__lang == undefined) {
            throw new Error(`Unsupported Language [${__lang}]`);
        }
        return __lang;
    }

    private static __languages: string[] = [];
    private static __languageTags: string[] = [];

    static get languages() {
        if (this.__languages.length == 0) {
            this.__languages = Object.keys(LanguageTags);
        }
        return this.__languages;
    }
    static get languageTags() {
        if (this.__languageTags.length == 0) {
            this.__languageTags = Object.keys(TagLanguages);
        }
        return this.__languageTags;
    }

    static languageFromTag(tag: string): Languages | undefined {
        return TagLanguages[tag];
    }
    static tagFromLanguage(lang: Languages): string | undefined {
        return LanguageTags[lang];
    }
    static async prepare(options: {
        source: Languages;
        target: Languages;
        downloadIfNeeded?: boolean;
    }) {
        if (NativeMLKitTranslator == null) return false;
        let __options: any = { ...options };
        let __source = this.getLanguageTag(options.source);
        let __target = this.getLanguageTag(options.target);
        __options.source = __source;
        __options.target = __target;
        return await NativeMLKitTranslator.prepare(__options);
    }
    static async translate(text: string) {
        if (NativeMLKitTranslator == null) return "";
        return await NativeMLKitTranslator.translate(text);
    };
    /**
     * 
     * @returns Array<Language-Tag>
     */
    static async getDownloadedLanguageModels() {
        if (NativeMLKitTranslator == null) return [];
        return await NativeMLKitTranslator.getDownloadedLanguageModels();
    }

    static async deleteLanguageModel(lang: Languages): Promise<boolean> {
        if (NativeMLKitTranslator == null) return false;
        let __lang = this.getLanguageTag(lang);
        let ok = await NativeMLKitTranslator.deleteLanguageModel(__lang);
        return isAndroid ? ok : (ok > 0);
    };

    static async downloadLanguageModel(lang: Languages): Promise<boolean> {
        if (NativeMLKitTranslator == null) return false;
        let __lang = this.getLanguageTag(lang);
        let ok = await NativeMLKitTranslator.downloadLanguageModel(__lang);
        return isAndroid ? ok : (ok > 0);
    }

    static async isLanguageDownloaded(lang: Languages): Promise<boolean> {
        if (NativeMLKitTranslator == null) return false;
        let __lang = this.getLanguageTag(lang);
        let ok = await NativeMLKitTranslator.isLanguageDownloaded(__lang);
        return isAndroid ? ok : (ok > 0);
    }

    static async setIdentifyConfidence(confidence: number) {
        if (NativeMLKitTranslator == null) return;
        return await NativeMLKitTranslator.setIdentifyConfidence(confidence);
    }

    /**
     * 
     * @param text 
     * @returns Language-Tag
     */
    static async identify(text: string) {
        if (NativeMLKitTranslator == null) return "";
        return await NativeMLKitTranslator.identify(text);
    }

    /**
     * 
     * @param text 
     * @returns Object{lang:Language-Tag,confidence:number}
     */
    static async identifyPossible(text: string) {
        let empty: Array<{ lang: string; confidence: number }> = [];
        if (NativeMLKitTranslator == null) return empty;
        return await NativeMLKitTranslator.identifyPossible(text);
    }
}