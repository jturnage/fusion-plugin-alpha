
#import "FusionResult.h"
#import "FusionPlugin.h"
#import "ControllerImagePreview.h"

@implementation ControllerImagePreview

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
  self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
  return self;
}

-(IBAction) cancel:(id)sender forEvent:(UIEvent *)event {
  ControllerCaptureReview* parent = (ControllerCaptureReview *)self.parentViewController;
  [parent retakePicture:self];
}

-(IBAction) savePicture:(id)sender forEvent:(UIEvent *)event {
  NSData* imageData = UIImageJPEGRepresentation(self.movieImage, 1.f);
  
  FusionResult* result = [[FusionResult alloc] init];
  [result setCapturedImage:YES];
  [result setCapturedVideo:YES];
  [result setVideoUrl:self.movieUrl];
  [result setVideoTimestamp:self.movieTime];
  [result setVideoImage:[imageData base64EncodedStringWithOptions:kNilOptions]];
  
  [self.plugin captured:result];
}

-(void) viewDidLoad {
  [super viewDidLoad];
  
  movieImageView = [[UIImageView alloc] initWithImage:self.movieImage];
  movieImageView.contentMode = UIViewContentModeScaleAspectFit;
}

-(void) viewWillAppear:(BOOL)animated {
  [super viewWillAppear:animated];
  
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.imageView addSubview:movieImageView];
  });
}

-(void) viewDidLayoutSubviews {
  [super viewDidLayoutSubviews];
  
  [movieImageView layoutIfNeeded];
  movieImageView.frame = self.imageView.bounds;
}

-(void) viewWillDisappear:(BOOL)animated {
  [super viewWillDisappear:animated];
}

@end