
class TweetViewReplyController: UIViewController ,TWTRTweetViewDelegate {
    
    // MARK: - Private Variables
    
    private lazy var tweetContainerView: UIScrollView = {
        let view = UIScrollView()
        view.backgroundColor = .white
        view.translatesAutoresizingMaskIntoConstraints = false
        view.alwaysBounceVertical = true
        return view
    }()
    
    private var tweetView: TWTRTweetView?
    public var tweet: TWTRTweet?
    
    // MARK: - UIViewController
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        title = "Tweet View"
        view.addSubview(tweetContainerView)
        self.configureTweetView(with: tweet!)
        //edgesForExtendedLayout = []
        
//        SVProgressHUD.setDefaultStyle(.dark)
//        SVProgressHUD.show()
//        loadTweet(with: "864977390941814784") { [weak self] (tweet, error) in
//            SVProgressHUD.dismiss()
//            if let error = error, let weakSelf = self {
//                UIAlertController.showAlert(with: error, on: weakSelf)
//            } else if let tweet = tweet {
//                self?.configureTweetView(with: tweet)
//            }
//        }
    }
    
    override func viewWillLayoutSubviews() {
        tweetContainerView.setNeedsLayout()
        tweetContainerView.layoutIfNeeded()
        tweetContainerView.contentSize = (tweetView?.bounds.size)!
    }
    
    // MARK: - Private Methods
    
    private func loadTweet(with tweetID: String, _ completion: @escaping (TWTRTweet?, Error?) -> Void) {
        guard let session = TWTRTwitter.sharedInstance().sessionStore.session() else {
            completion(nil, TweetViewErrors.noAccount)
            return
        }
        
        TWTRAPIClient(userID: session.userID).loadTweet(withID: tweetID, completion: completion)
    }
    
    private func configureTweetView(with tweet: TWTRTweet) {
        self.tweet = tweet
        
        if let tweetView = tweetView {
            tweetView.removeConstraints(tweetView.constraints)
            tweetView.removeFromSuperview()
            self.tweetView = nil
        }
        let newTweetView = TWTRTweetView(tweet: tweet, style: TWTRTweetViewStyle.regular)
        newTweetView.showActionButtons = true;
        newTweetView.theme = TWTRTweetViewTheme.light;
        newTweetView.translatesAutoresizingMaskIntoConstraints = false
        newTweetView.delegate = self
        tweetContainerView.addSubview(newTweetView)
        self.tweetView = newTweetView
        setupTweetContainerView()
        setupTweetView()
    }
    
    private func setupTweetView() {
        if let tweetView = tweetView {
//            tweetView.centerXAnchor.constraint(equalTo: tweetContainerView.centerXAnchor).isActive = true
//            tweetView.centerYAnchor.constraint(equalTo: tweetContainerView.centerYAnchor).isActive = true
//
//            let size = tweetView.sizeThatFits(tweetContainerView.frame.size)
//            tweetView.heightAnchor.constraint(equalToConstant: size.height).isActive = true
            tweetView.widthAnchor.constraint(equalToConstant: view.bounds.size.width).isActive = true
            tweetView.topAnchor.constraint(equalTo: tweetContainerView.topAnchor, constant: 0.0).isActive = true
            //tweetView.bottomAnchor.constraint(equalTo: tweetContainerView.bottomAnchor, constant: 0.0).isActive = true
//            tweetView.rightAnchor.constraint(equalTo: tweetContainerView.rightAnchor, constant: 0.0).isActive = true
            tweetView.leftAnchor.constraint(equalTo: tweetContainerView.leftAnchor, constant: 0.0).isActive = true
            
        }
    }
    
    private func setupTweetContainerView() {
        tweetContainerView.topAnchor.constraint(equalTo: view.topAnchor, constant: 0.0).isActive = true
        tweetContainerView.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: 0.0).isActive = true
        tweetContainerView.rightAnchor.constraint(equalTo: view.rightAnchor, constant: 0.0).isActive = true
        tweetContainerView.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 0.0).isActive = true
    }
    func tweetView(_ tweetView: TWTRTweetView, didReplyTap tweet: TWTRTweet) {
        print("reply tapped!");
        //let composer = TWTRComposerViewController.composer(with: tweet)
        let name = tweet.author.screenName
        let name2 = tweet.retweeted?.author.screenName
        let names = [name, name2]
        let composer = TWTRComposerViewController.initWithReply(tweet.tweetID, names: names as! [String])
        
        composer.delegate = self as? TWTRComposerViewControllerDelegate
        
        present(composer, animated: true, completion: nil)
    }
}
