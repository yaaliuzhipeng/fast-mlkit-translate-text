
#ifdef RCT_NEW_ARCH_ENABLED
#import <RNFastMlkitTranslateTextSpec/RNFastMlkitTranslateTextSpec.h>

@interface FastMlkitTranslateText : NSObject <NativeFastMlkitTranslateTextSpec>
#else
#import <React/RCTBridgeModule.h>

@interface FastMlkitTranslateText : NSObject <RCTBridgeModule>
#endif

@end
