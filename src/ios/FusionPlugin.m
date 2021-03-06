
#import "FusionExercise.h"
#import "FusionResult.h"
#import "FusionPlugin.h"

@implementation FusionPlugin
@synthesize hasPendingOperation;

-(void) takeVideo:(CDVInvokedUrlCommand *)command {
  hasPendingOperation = YES;
  [self identifyExercise:[command.arguments objectAtIndex:0]];
  [self identifySettings:[command.arguments objectAtIndex:1]];

  ControllerCaptureOverlay* controller = [[ControllerCaptureOverlay alloc] initWithNibName:@"ControllerCaptureOverlay" bundle:nil];
  [controller setPlugin:self];

  [self setCommand:command];
  [self.viewController presentViewController:controller animated:YES completion:nil];
}

-(void) playVideo:(CDVInvokedUrlCommand *)command {
  hasPendingOperation = YES;
  [self identifyExercise:[command.arguments objectAtIndex:0]];

  ControllerCaptureReview* controller = [[ControllerCaptureReview alloc] initWithNibName:@"ControllerCaptureReview" bundle:nil];
  [controller setPlugin:self];
  
  [self setCommand:command];
  [self.viewController presentViewController:controller animated:YES completion:nil];
}

-(void) cancelled {
  FusionResult* result = [[FusionResult alloc] init];
  [result setCancelled:YES];

  [self captured:result];
  [self removeTempFiles];
  hasPendingOperation = NO;
}

-(void) failed:(NSString *)message {
  [self.commandDelegate sendPluginResult:[CDVPluginResult 
    resultWithStatus:CDVCommandStatus_ERROR
    messageAsString:message] callbackId:self.command.callbackId];

  [self removeTempFiles];

  hasPendingOperation = NO;
  [self.viewController dismissViewControllerAnimated:YES completion:nil];
}

-(void) captured:(FusionResult *)result {
  NSError* error;
  NSString* message = [result toJSON:&error];

  if (error.code != noErr) {
    [self.commandDelegate sendPluginResult:[CDVPluginResult 
      resultWithStatus:CDVCommandStatus_ERROR
      messageAsString:[error localizedDescription]] callbackId:self.command.callbackId];
  } else {
    [self.commandDelegate sendPluginResult:[CDVPluginResult
      resultWithStatus:CDVCommandStatus_OK
      messageAsString:message] callbackId:self.command.callbackId];
  }

  [self removeTempFiles];

  hasPendingOperation = NO;
  [self.viewController dismissViewControllerAnimated:YES completion:nil];
}

-(void) identifyExercise:(NSString *)json {
  NSError* error;
  NSData* data = [json dataUsingEncoding:NSUTF8StringEncoding];

  id jsonData = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
  if (jsonData == nil)
    return;

  id videoUrl = jsonData[@"videoUrl"];
  [self setCurrentVideoUrl:(videoUrl != nil) ? [[NSURL alloc] initWithString:videoUrl] : nil];
  [self setExercise:[[FusionExercise alloc] initWithData:jsonData]];
}

-(void) identifySettings:(NSString *)json {
  NSError* error;
  NSData* data = [json dataUsingEncoding:NSUTF8StringEncoding];

  id jsonData = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
  if (jsonData == nil)
    return;

  id endpointUrl = jsonData[@"endpointUrl"];
  [self setUploadEndpointUrl:(endpointUrl != nil) ? [[NSURL alloc] initWithString:endpointUrl] : nil];

  NSDictionary* headers = jsonData[@"headers"];
  if (headers == nil)
    return;

  [self setApiVersion:headers[@"X-Api-Version"]];
  [self setApiAuthorize:headers[@"Authorization"]];
}

-(void) removeTempFiles {
  NSString* directory = NSTemporaryDirectory();
  NSArray* collection = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:directory error:NULL];
  for (NSString* fileName in collection) {
    [[NSFileManager defaultManager] removeItemAtPath:[NSString stringWithFormat:@"%@%@", directory, fileName] error:NULL];
  }

  [[NSURLCache sharedURLCache] removeAllCachedResponses];
}

@end