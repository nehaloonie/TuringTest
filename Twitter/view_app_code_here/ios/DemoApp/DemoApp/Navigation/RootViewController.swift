//
//  RootViewController.swift
//  DemoApp
//
//  Created by Rajul Arora on 10/26/17.
//  Copyright © 2017 Twitter. All rights reserved.
//

import UIKit

@objc protocol RootViewControllerDelegate {
    func loginViewController(viewController: RootViewController, didAuthWith session: TWTRSession)
    func loginViewControllerDidClearAccounts(viewController: LoginViewController)
}

class RootViewController: UIViewController {
    weak var delegate: RootViewControllerDelegate?
    // MARK: - Private Variables
    private lazy var loginButton: TWTRLogInButton = { [unowned self] in
        let button = TWTRLogInButton() { (session, error) in
            if let error = error {
                UIAlertController.showAlert(with: error, on: self)
                self.navigateToHome()
            } else if let session = session {
                self.navigateToHome();
//                self.dismiss(animated: true) {
//                    self.delegate?.loginViewController(viewController: self, didAuthWith: session)
//                    self.navigateToHome()
//                }
            }
        }
        
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()
    
    private var currentViewController: UIViewController?
    private var animationController: RootAnimationController = RootAnimationController()

    // MARK: - UIViewController

    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.addSubview(loginButton)
        setupLoginButton()
        //navigateToHome()
    }
    private func setupLoginButton() {
        loginButton.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -10.0).isActive = true
        loginButton.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 10.0).isActive = true
        loginButton.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -100).isActive = true
        loginButton.heightAnchor.constraint(equalToConstant: 50).isActive = true

    }
    // MARK: - Private Methods

    fileprivate func navigateToHome() {
        let homeViewController = UserTimelineViewController()
        //homeViewController.delegate = self
        let navigationController = UINavigationController(rootViewController: homeViewController)
        setCurrentViewController(with: navigationController)
    }

    fileprivate func navigateToAuthentication() {
        let authenticationViewController = AuthenticationViewController()
        authenticationViewController.delegate = self
        let navigationController = UINavigationController(rootViewController: authenticationViewController)
        setCurrentViewController(with: navigationController)
    }

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

// MARK: - HomeViewControllerDelegate

extension RootViewController: HomeViewControllerDelegate {
    func homeViewControllerDidTapProfileButton(viewController: HomeViewController) {
        navigateToAuthentication()
    }
}

// MARK: - AuthenticationViewControllerDelegate

extension RootViewController: AuthenticationViewControllerDelegate {
    func authenticationViewControllerDidTapHome(viewController: AuthenticationViewController) {
        navigateToHome()
    }
}
