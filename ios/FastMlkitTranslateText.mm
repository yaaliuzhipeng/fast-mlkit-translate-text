#import "FastMlkitTranslateText.h"
#import <GoogleMLKit/MLKit.h>

NSString *CODE_IDENTIFY_FAIL = @"-1000";
NSString *CODE_IDENTIFY_FAIL_UND = @"-1001";
NSString *CODE_CHECK_DOWNLOAD_FAIL = @"-2000";
NSString *CODE_DOWNLOAD_FAIL = @"-2001";
NSString *CODE_DOWNLOADED_MODELS_FAIL = @"-2002";
NSString *CODE_LANG_NULL = @"-3000";
NSString *CODE_LANG_TRANSLATE_FAIL = @"-3001";

@implementation FastMlkitTranslateText
{
    MLKLanguageIdentification *identifier;
    MLKTranslator *translator;
    NSNumber *downloadIfNeeded;
    NSMutableDictionary<NSString *, id> *downloadObservers;
    NSProgress *progress;
}
RCT_EXPORT_MODULE()

- (instancetype)init
{
    self = [super init];
    if (self) {
        downloadIfNeeded = @0;
        downloadObservers = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (void) ensureIdentifierInitialized{
    if (identifier == nil) {
        identifier = [MLKLanguageIdentification languageIdentification];
    }
}

RCT_EXPORT_METHOD(deleteLanguageModel:(NSString *)lang resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    MLKTranslateRemoteModel *model = [MLKTranslateRemoteModel translateRemoteModelWithLanguage:lang];
    BOOL isDownloaded = [[MLKModelManager modelManager] isModelDownloaded: model];
    if(isDownloaded == YES) {
        [[MLKModelManager modelManager] deleteDownloadedModel:model completion:^(NSError * _Nullable error) {
            //ignore result
        }];
    }
    resolve(@1);
}

RCT_EXPORT_METHOD(handleRemoveObserver: (NSString *)lang withType:(NSString *) type){
    NSString *key =[NSString stringWithFormat:@"%@-%@", lang, type];
    id __failObserver = [downloadObservers valueForKey: key];
    if(__failObserver != nil) {
        [NSNotificationCenter.defaultCenter removeObserver:__failObserver];
        [downloadObservers removeObjectForKey:key];
    }
}

RCT_EXPORT_METHOD(downloadLanguageModel:(NSString *)lang resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    MLKTranslateRemoteModel *model = [MLKTranslateRemoteModel translateRemoteModelWithLanguage:lang];
    BOOL isDownloaded = [[MLKModelManager modelManager] isModelDownloaded: model];
    if(isDownloaded == YES){
        resolve(@1);
        return;
    }
    MLKModelDownloadConditions *conditions = [[MLKModelDownloadConditions alloc]
                                              initWithAllowsCellularAccess:YES
                                              allowsBackgroundDownloading:YES];
    id successObserver = [NSNotificationCenter.defaultCenter addObserverForName:MLKModelDownloadDidSucceedNotification object:nil queue:nil usingBlock:^(NSNotification * _Nonnull notification) {
        if(notification.userInfo == nil) {
            [self handleRemoveObserver:lang withType:@"success"];
            return;
        }
        MLKTranslateRemoteModel *downloadModel = notification.userInfo[MLKModelDownloadUserInfoKeyRemoteModel];
        if([model isKindOfClass:[MLKTranslateRemoteModel class]] && downloadModel == model) {
            //download success
            resolve(@1);
        }
        [self handleRemoveObserver:lang withType:@"success"];
    }];
    [downloadObservers setValue: successObserver forKey: [NSString stringWithFormat:@"%@-%@", lang, @"success"]];
    id failObserver = [NSNotificationCenter.defaultCenter addObserverForName:MLKModelDownloadDidFailNotification object:nil queue:nil usingBlock:^(NSNotification * _Nonnull notification) {
        if(notification.userInfo == nil) {
            [self handleRemoveObserver:lang withType:@"fail"];
            return;
        }
        NSError *error = notification.userInfo[MLKModelDownloadUserInfoKeyError];
        if(error != nil) {
            reject(CODE_DOWNLOAD_FAIL, error.localizedDescription, nil);
        }
        [self handleRemoveObserver:lang withType:@"fail"];
    }];
    [downloadObservers setValue: failObserver forKey: [NSString stringWithFormat:@"%@-%@", lang, @"fail"]];
    progress = [[MLKModelManager modelManager] downloadModel:model conditions:conditions];
    
}

RCT_EXPORT_METHOD(getDownloadedLanguageModels:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    NSMutableArray *array = [[NSMutableArray alloc] init];
    NSSet<MLKTranslateRemoteModel *> *set = [MLKModelManager modelManager].downloadedTranslateModels;
    MLKTranslateRemoteModel *model;
    NSEnumerator *en = [set objectEnumerator];
    while (model = [en nextObject]) {
        [array addObject:model.language];
    }
    resolve(array);
}

RCT_EXPORT_METHOD(identify:(NSString *)text resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    NSLog(@"identify text: %@",text);
    [self ensureIdentifierInitialized];
    [identifier identifyLanguageForText:text completion:^(NSString * _Nullable languageTag, NSError * _Nullable error) {
        if (error != nil) {
            //failed with error
            reject(CODE_IDENTIFY_FAIL, error.localizedDescription,nil);
            return;
        }
        if(![languageTag isEqualToString:@"und"]){
            resolve(languageTag);
            NSLog(@"what the hell ??? %@", languageTag);
        }else{
            //no language identified
            reject(CODE_IDENTIFY_FAIL_UND,error.localizedDescription, nil);
        }
    }];
}

RCT_EXPORT_METHOD(identifyPossible:(NSString *)text resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [self ensureIdentifierInitialized];
    [identifier identifyPossibleLanguagesForText:text completion:^(NSArray<MLKIdentifiedLanguage *> * _Nullable identifiedLanguages, NSError * _Nullable error) {
        if (error != nil) {
            //failed with error
            reject(CODE_IDENTIFY_FAIL, error.localizedDescription,nil);
            return;
        }
        NSMutableArray *array = [[NSMutableArray alloc] init];
        for (MLKIdentifiedLanguage *identifiedLanguage in identifiedLanguages) {
            [array addObject: @{
                @"lang": identifiedLanguage.languageTag,
                @"confidence": @(identifiedLanguage.confidence),
            }];
        }
        resolve(array);
    }];
}

RCT_EXPORT_METHOD(isLanguageDownloaded:(NSString *)lang resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    MLKTranslateRemoteModel *model = [MLKTranslateRemoteModel translateRemoteModelWithLanguage:lang];
    BOOL isDownloaded = [[MLKModelManager modelManager] isModelDownloaded: model];
    if(isDownloaded == YES){
        resolve(@1);
    }else{
        resolve(@0);
    }
}

RCT_EXPORT_METHOD(setIdentifyConfidence:(double)confidence) {
    MLKLanguageIdentificationOptions *options = [[MLKLanguageIdentificationOptions alloc] initWithConfidenceThreshold: confidence];
    identifier = [MLKLanguageIdentification languageIdentificationWithOptions:options];
}

RCT_EXPORT_METHOD(translate:(NSString *)text resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    if ([downloadIfNeeded isEqualToNumber:@1]) {
        [translator
         downloadModelIfNeededWithConditions:[[MLKModelDownloadConditions alloc] initWithAllowsCellularAccess:YES allowsBackgroundDownloading:YES]
         completion:^(NSError * _Nullable error) {
            if (error != nil) {
                reject(CODE_DOWNLOAD_FAIL, error.localizedDescription,nil);
                return;
            }
            [self doTranslate:text resolve:resolve reject:reject];
        }];
    }else{
        [self doTranslate:text resolve:resolve reject:reject];
    }
}

- (void) doTranslate:(NSString *)text resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject{
    [translator translateText:text completion:^(NSString * _Nullable result, NSError * _Nullable error) {
        if(error != nil) {
            reject(CODE_LANG_TRANSLATE_FAIL,error.localizedDescription, nil);
        }else if(result == nil) {
            reject(CODE_LANG_TRANSLATE_FAIL,@"translation failed",nil);
        }else{
            //success
            resolve(result);
        }
    }];
}

/**
 MLKTranslateLanguageChinese 是language tag、例如: zh , en
 */
#ifdef RCT_NEW_ARCH_ENABLED
- (void)prepare:(JS::NativeFastMlkitTranslateText::SpecPrepareOptions &)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    NSString *source = options.source();
    NSString *target = options.target();
    std::optional<bool> boolDownloadIfNeeded = options.downloadIfNeeded();
    if (boolDownloadIfNeeded.value_or(false) == true) {
        downloadIfNeeded = @1;
    }else{
        downloadIfNeeded = @0;
    }
    if (source != nil && target != nil) {
        if(translator != nil) {
            translator = nil;
        }
        MLKTranslatorOptions *options = [[MLKTranslatorOptions alloc] initWithSourceLanguage:source targetLanguage:target];
        translator = [MLKTranslator translatorWithOptions:options];
        resolve(@1);
    }else{
        reject(CODE_LANG_NULL,@"source or target language not supported,or you passed wrong language tag",nil);
    }
}
#else
RCT_EXPORT_METHOD(prepare:(NSDictionary *)options
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject){
    NSString *source;
    NSString *target;
    if (options != nil) {
        source = [options valueForKey:@"source"];
        target = [options valueForKey:@"target"];
        BOOL boolDownloadIfNeeded = [options valueForKey:@"downloadIfNeeded"];
        if(boolDownloadIfNeeded) {
            downloadIfNeeded = @1;
        }else{
            downloadIfNeeded = @0;
        }
    }
    if (source != nil && target != nil) {
        if(translator != nil) {
            translator = nil;
        }
        MLKTranslatorOptions *options = [[MLKTranslatorOptions alloc] initWithSourceLanguage:source targetLanguage:target];
        translator = [MLKTranslator translatorWithOptions:options];
        resolve(@1);
    }else{
        reject(CODE_LANG_NULL,@"source or target language not supported,or you passed wrong language tag",nil);
    }
}
#endif


#ifdef RCT_NEW_ARCH_ENABLED
//ignore
#else

#endif


+ (BOOL)requiresMainQueueSetup
{
    return YES;
}


// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeFastMlkitTranslateTextSpecJSI>(params);
}
#endif

@end
