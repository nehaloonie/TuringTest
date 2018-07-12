//
//  UserTimelineViewController.swift
//  FabricSampleApp
//
//  Created by Steven Hepting on 2/2/15.
//  Copyright (c) 2015 Twitter. All rights reserved.
//

import UIKit

class UserTimelineViewController: TWTRTimelineViewController, TWTRTweetViewDelegate {
    
    private var currentViewController: UIViewController?
    private var animationController: RootAnimationController = RootAnimationController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tweetViewDelegate = self;
        let client = TWTRAPIClient.withCurrentUser()
        self.dataSource = TWTRUserTimelineDataSource(screenName: "picturecats", apiClient: client)
        
        self.title = "@picturecats"
        self.showTweetActions = true
        
        
    }
    func tweetView(_ tweetView: TWTRTweetView, didTap tweet: TWTRTweet) {
        let replyViewController = TweetViewReplyController()
        replyViewController.tweet = tweet
        //homeViewController.delegate = self
        //let navigationController = UINavigationController(rootViewController: homeViewController)
        //setCurrentViewController(with: navigationController)
        navigationController?.pushViewController(replyViewController, animated: true)
        //navigateToReply();
    }
    
    func tweetView(_ tweetView: TWTRTweetView, didReplyTap tweet: TWTRTweet) {
        print("reply tapped!");
        //let composer = TWTRComposerViewController.composer(with: tweet)
        let name = tweet.author.screenName
        let name2 = tweet.retweeted?.author.screenName
        let names = [name, name2]
        let composer = TWTRComposerViewController.initWithReply(tweet.tweetID, names: names as! [String])
        
        composer.delegate = self
        
        present(composer, animated: true, completion: nil)
    }
//    fileprivate func navigateToReply() {
//        _ = TweetViewReplyController()
//        //homeViewController.delegate = self
//        //let navigationController = UINavigationController(rootViewController: homeViewController)
//        //setCurrentViewController(with: navigationController)
//        //navigationController?.pushViewController(homeViewController, animated: true)
//    }
    
    private func setCurrentViewController(with viewController: UIViewController) {
        var transitionContext = RootTransitionContext(from: currentViewController, to: viewController, in: self)
        transitionContext.animationCompletion = { [weak self] in
            self?.currentViewController = viewController
            self?.setupConstraints()
        }
        
        animationController.transition(using: transitionContext)
    }
    private func setupConstraints() {
        guard let viewController = currentViewController else { return }
        viewController.view.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        viewController.view.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        viewController.view.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        viewController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
    }
}
extension UserTimelineViewController: TWTRComposerViewControllerDelegate {
    func composerDidCancel(_ controller: TWTRComposerViewController) {
        dismiss(animated: false, completion: nil)
    }
    
    func composerDidFail(_ controller: TWTRComposerViewController, withError error: Error) {
        dismiss(animated: false, completion: nil)
    }
    
    func composerDidSucceed(_ controller: TWTRComposerViewController, with tweet: TWTRTweet) {
        dismiss(animated: false, completion: nil)
    }
}
