# reddit-composer
This is an AngularJS/Material, Spring Boot web app that allows Reddit users to compose batch messages using their developer API credentials.

The app uses Handsontable to allow users to input data easily from other sources (Google Docs, Excel, etc) by supporting cell range copy/paste. 

The app also provides real-time processing feedback through WebSockets. The Spring Boot app uses the simple broker auto-configuration, and the client uses a short library I've written using SockJS (see WebSocketClient.js).

The Reddit API is accessed using the JRAW java library (The Java Reddit API Wrapper https://thatjavanerd.github.io/JRAW/). 

If you'd like to quickly use the webapp, download reddit-composer.zip. Extract both files to any folder. If you're running on Windows, run the batch file included. This will start the Spring Boot app and automatically launch your browser to the web app's main page. 

Alternatively, you can can just run RedditComposerApplication.java as a Java/Spring Boot application within your IDE of choice. 

![alt tag](https://raw.githubusercontent.com/williamwilliamwilliam/reddit-composer/master/messenger.gif)
