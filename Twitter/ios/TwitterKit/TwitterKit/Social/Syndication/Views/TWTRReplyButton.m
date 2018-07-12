/*
 * Copyright (C) 2017 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#import "TWTRReplyButton.h"
#import <TwitterCore/TWTRColorUtil.h>
#import <TwitterCore/TWTRUtils.h>
#import "TWTRFontUtil.h"
#import "TWTRImages.h"
#import "TWTRNotificationCenter.h"
#import "TWTRNotificationConstants.h"
#import "TWTRTranslationsUtil.h"
#import "TWTRTweet.h"
#import "TWTRTweetShareItemProvider.h"
#import "TWTRTweetView_Private.h"
#import "TWTRTwitter_Private.h"

@interface TWTRReplyButton ()

@property (nonatomic) TWTRTweet *tweet;
@property (nonatomic) TWTRReplyButtonSize replyButtonSize;

@end

@implementation TWTRReplyButton
@synthesize scribeViewName = _scribeViewName;

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        _replyButtonSize = TWTRReplyButtonSizeRegular;
        [self replyButtonCommonInit];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    return [self initWithFrame:frame replyButtonSize:TWTRReplyButtonSizeRegular];
}

- (instancetype)initWithReplyButtonSize:(TWTRReplyButtonSize)size
{
    return [self initWithFrame:CGRectZero replyButtonSize:size];
}

- (instancetype)initWithFrame:(CGRect)frame replyButtonSize:(TWTRReplyButtonSize)size
{
    self = [super initWithFrame:frame];
    if (self) {
        _replyButtonSize = size;
        [self replyButtonCommonInit];
    }
    return self;
}

- (void)replyButtonCommonInit
{
    self.accessibilityLabel = TWTRLocalizedString(@"tw__close_button");

    UIImage *image;
    switch (self.replyButtonSize) {
        case TWTRReplyButtonSizeRegular:
            image = [TWTRImages lightRetweet];
            break;
        case TWTRReplyButtonSizeLarge:
            image = [TWTRImages lightRetweet];
            break;
    }
    [self setImage:image forState:UIControlStateNormal];
    [self addTarget:self action:@selector(replyButtonTapped) forControlEvents:UIControlEventTouchUpInside];
    self.presenterViewController = [TWTRUtils topViewController];
}

- (void)configureWithTweet:(TWTRTweet *)tweet
{
    self.tweet = tweet;
}

- (void)setPresenterViewController:(UIViewController *)presenterViewController
{
    _presenterViewController = presenterViewController ?: [TWTRUtils topViewController];
}

#pragma mark - Sharing

- (void)replyButtonTapped
{
    if (!self.tweet) {
        return;
    }
    [_delegate replyTapped:self.tweet];
//    [[TWTRTwitter sharedInstance].scribeSink didShareTweetWithID:self.tweet.tweetID forUserID:self.tweet.perspectivalUserID fromViewName:self.scribeViewName];
//
//    [TWTRNotificationCenter postNotificationName:TWTRWillShareTweetNotification tweet:self.tweet userInfo:nil];
//
//    TWTRTweetShareItemProvider *shareItemProvider = [[TWTRTweetShareItemProvider alloc] initWithTweet:self.tweet];
//    UIActivityViewController *activityVC = [[UIActivityViewController alloc] initWithActivityItems:@[shareItemProvider] applicationActivities:nil];
//    activityVC.completionWithItemsHandler = ^(NSString *activityType, BOOL completed, NSArray *returnedItems, NSError *activityError) {
//        NSString *notificationName = completed ? TWTRDidShareTweetNotification : TWTRCancelledShareTweetNotification;
//        [TWTRNotificationCenter postNotificationName:notificationName tweet:self.tweet userInfo:nil];
//    };

//    [self presentActivityViewController:activityVC];
}

- (void)presentActivityViewController:(UIActivityViewController *)activityViewController
{
    if ([self shouldPresentShareSheetUsingPopover]) {
        activityViewController.modalPresentationStyle = UIModalPresentationPopover;
        [self.presenterViewController presentViewController:activityViewController animated:YES completion:nil];

        UIPopoverPresentationController *presentationController = [activityViewController popoverPresentationController];
        presentationController.sourceRect = self.bounds;
        presentationController.sourceView = self;
    } else {
        [self.presenterViewController presentViewController:activityViewController animated:YES completion:nil];
    }
}

#pragma mark - Helpers

- (BOOL)shouldPresentShareSheetUsingPopover
{
    return ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPad);
}

@end
