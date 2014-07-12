Coils - Build Neo4j Web Applications
====================================



Introduction
------------

Originally called Clojure-on-Coils, and now shortened to just "Coils", this framework was born out of one web developer's pain over many years developing both web and desktop software. Zubair Quraishi started professionally in the early 1990s as a C++ programmer, and then moved to Java, and got heavily involved in Java web applications, only to find the whole develop, compile, test loop hugely cumbersome and time wasting.

Then one day, like many developers before him, around 2008, he discovered Ruby on Rails.

Wow! Suddenly the world opened up for Zubair and he realised there was a better way to develop web software by using interactive languages such as Ruby and easy SQL queries via ActiveRecord. Soon however, he became frustrated again, as with Rails everything was generated on the server and control of the web page on the client was not fine-grained enough. So, in true developer style, Zubair took the long and painful route, going to Erlang, Clojure and Vaadin, Grails, JQuery, Javascript, GWT Google Closure, Dart, and finally deciding to use ClojureScript for a project he was involved with.

That project was NemCV, and it is the CV system for Denmark. Seeing that there was alot of good work in NemCV that could be reused Zubair took inspiration from Ruby on Rails, which coincidently also came from Denmark, where a developer called David Heinemeier Hansson made Ruby on Rails based on a project called BaseCamp which he had made for his company 37Signals. So thus, Coils was born!

After around a year of development of Coils Zubair realised that to make development even simpler the new breed of Graph Databases could be used, and decided to base Coils around Neo4j. Then he also discovered Facebook React and Reactive programming and incorporated this into Coils. However, there was still something missing, and this piece of the puzzle fell into place when Zubair's business partner in NemCV, Franco Soldera introduced Zubair to Meteor, a realtime Javascript framework which had both Reactive capabilities and also excellent databinding capabilities. In his opinion Meteor is the best full stack Javascript framework that he had seen (and yes, Zubair had seen Derby, Angular, Ember and Knockout). The reason he loved Meteor was not just because of its featues such as Reactive front end and databinding, but also because Meteor is opinionated, which solves the problem of having to continually choose which Javascript frameworks to choose, thereby saving alot of time wasted with experimenting with different Javascript frameworks and glueing them together.

Meteor's databinding principles use a database called MongoDB on the server and MiniMongo on the client, but Coils already had full client side data access to both relational databases and Neo4j. So the current goal of Coils is to make these databinding work more seamlessly with the frontend, just as Meteor does.






What is Coils?
-------------------------

Coils is a web framework where development is done in Clojure and Clojurescript and the software runs on the JVM so it can be deployed on any Java web or application server, including Jetty, Tomcat, JBoss, Glassfish, Weblogic, or Websphere.

A few notes:

- Uses Clojure for the server side
- Uses ClojureScript client-side
- Exclusively uses the LightTable IDE




Deprecated features from April 2013 to July 2014
------------------------------------------------
In 2013 Facebook created React, a Virtual Dom based Javascript library. David Nolen then created Om, a ClojureScript wrapper on top of React, which changed the ClojureScript client side story forever! Upon seeing Om I immediately knew that this was the future of ClojureScript development, using a Reactive GUI paradigm, also similar to Angular.js, Ember.js, and Meteor. So I had to take the tough decision to deprecate almost the whole UI that I had created in the previous version of Clojure on Coils. So the following features are now a thing of the past:

- Can use Crate for HTML.
- Uses Dommy.
- Uses Domina.
- JayQ for JQuery integration.
- Google Closure UI Library - the same library used to build Google.com, Gmail, and Google+.
- Google Maps integration for AJAX-based maps. (Can be swapped on/off.)

:and the following things stay:

- Interactive client and server side development with LightTable IDE.
- Integration with Mandrill for sending transactional emails.
- Twitter Bootstrap 3.0 for styling.
- Google Closure for advanced compression.
- clj-http for server side HTTP requests.
- SQL Korma for database requests.
- Neocons for Neo4j access.
- Compojure, Ring, and Shoreleave for server side code.
- core.async for a client-side synchronous programming model.

The reason for the discontinued features is that they all require explicit calls to manipulate the DOM, which is oppisite to the way that Facebook React works.

If you still wish to use the discontinued features then use an older version of the Coils framework.



Unique features
---------------

- Build the default app and then click on debug at the top and you can Browse the source code behind any GUI element by left clicking on the element

- Secure client Neo4j Cypher. All calls are encrypted and use a server side key to avoid Cypher injection attacks, yet at the same time the Cypher calls appearing in client side code are easy to understand.

- Secure client side SQL. All calls are encrypted and use a server side key to avoid SQL injection attacks, yet at the same time the SQL calls appearing in client side code are easy to understand.

- Web development without callback hell. Coils uses Clojure's core.async library to set of sychronous server side calls.

- Replay all web sessions as all sessions are recorded using Neo4j

All features
------------

- hard dependency on Neo4j now
- Clojurescript Om by David Nolen
- Records all web sessions for playback to understand customer behaviour (using Neo4j)
- AB testing built in
- Interactive client and server side development with LightTable IDE
- Integration with Mandrill for sending transactional emails
- Twitter Bootstrap 3.x for styling
- Google Closure for advanced compression
- clj-http for server side HTTP requests
- SQL Korma for database requests
- Neocons for Neo4j access
- Compojure, Ring, and Shoreleave for server side code
- core.async for a client-side synchronous programming model




Differences from Om
-------------------
The only part of Om that Coils uses is the rendering engine and the change listeners (for GUI playback). This means that Coils does not use the component local state features on Om. The implications of this are very important. For example, in Om when the end user clicks on a button in a react/Om component then the button event will be passed back via a core.sync channel. However, in Coils, when the user presses a button then the Application model used to generate the DOM itself is changed, with a property :click being set to true. This :click property is then watched via an event watcher to make any changes.




Project page
------------
The website for this project is online at [coils.cc](http://coils.cc).





Comparison with other Clojure web frameworks
--------------------------------------------

[Hoplon](http://http://hoplon.io/) - In my mind Hoplon is the most complete of all the Clojure web frameworks, and I think that Coils has alot of catching up to do to get as full featured as Hoplon. Hoplon uses Reactive programming and has an amazing is web designer friendly as GUIs can be made from HTML, instead of Clojurescript

<br>


[Luminus](http://www.luminusweb.net/) - The main difference between Luminus and Clojure-on-Coils is that Luminus uses a HTML templating system for rendering web pages, whereas Coils currently only supports rendering from within Clojure itself. This makes Luminus at the moment a better choice for companies with seperate design and development teams. Designers will be more comfortable working in clean HTML files.

<br>

[Pedestal](http://pedestal.io/) - Pedestal is an amazing Clojure web framework made by the main Clojure developers at [Relevance](http://thinkrelevance.com/), now called Cognitect. It has a number of differences to Coils, but in 2014 the front end part of Pedestal was dropped, probably to be replaced by Facebook React/Om. The frontend of Coils is actually based on the pedestal model, which did so much right, especially having a seperate data and UI model





Installation
------------

    - Install Coils from Github:

    git clone  https://github.com/zubairq/coils.git  my_new_application



    - Then install Neo4j:

    http://neo4j.com
    http://www.zubairquraishi.com/zubairquraishi/clojure-neocons-for-neo4j.html




Anatomy of a Coils application
------------------------------

    my_new_application
        └ README.md
          project.clj

        └ srcbase
            └ webapp_config
                └ settings.clj

          src
              └ webapp
                  └ client
                      └ views
                          └ page_structure.cljs
                            ... more views ...

                        main.cljs
                        gui_actions.cljs
                        session.cljs
                        actions.cljs

                             ...
                    server
                        └ fns.clj - server side functions go here

                    framework
                        └ client
                              ...
                          server

          resources
              └ public
                main.html


    srcprod
        └ webapp_config
            └ settings.clj

    srctest
        └ webapp_config
            └ settings.clj



Getting started
---------------

1) Compile clojurescript and Start the self-refreshing web server:

    cd my_new_application
    lein with-profile base cljsbuild clean
    lein with-profile base cljsbuild once
    lein with-profile base ring server

2) Open LightTable.

3) Add the project root directory.

4) Open a Browser tab and point to [127.0.0.1:3000](127.0.0.1:3000).

The easiest way to get started is to just play around with the demo app online at http://coils.cc. Click on the logo in the top left and then you enter the debug mode. You can click on most elements on the page and you can see the code used to generate them.




Adding something to the web page
--------------------------------

1) Go to a clojurescript view in src/webapp/client/views/main_view.cljs.

2) Add to main_view.cljs:

    (swap-section "main" "<div>hello world</div>")

3) Press Ctrl-Alt-Enter on the line and the view should swap out the whole page with the text "Hello world"


Adding debuggable elements to the page
--------------------------------------

    (defn-html logged-in-panel []
        (el :div {:class "row" :style "padding: 5px; width:400px;"} [
            (el :div
                          {:id    "signed-in-as-text"
                           :text  "Signing in..."
                           :class "pull-left"
                           :style "margin-right: 20px; margin-top:10px;"
             })

             (el :button
                          {:id      "logout-button"
                           :class   "btn btn-default "
                           :text    "Logout"
                           :style   "margin-right: 20px;"
                           :onclick #(do-action "show login signup panel")})

             (el :button
                          {:id      "settings-button"
                           :class   "btn btn-default"
                           :text    "Settings"
                           :style   "margin-right: 20px;"
                          })
    ]))


    (swap-section "main" (logged-in-panel))



Client side message passing system (a.k.a. events)
-----------------------------------------------

Define an action:

    (redefine-action "Say something"
        (js/alert "Hello"))


Call an action:

    (do-action "Say something")



Calling server side code
------------------------

From the client side:

    (go
         (js/alert
             (:text (<! (remote "say-hello" {:name "Johnny"})))))



Define in fns.clj on the server side (using core.async):

    (defn say-hello [{name :name}]
        {:text (str "Hello " name))})







Client side SQL
---------------

    (go
        (.log js/console (str (<! (sql "SELECT * FROM test_table where name = ?" ["shopping"] )))))


Please note that the raw SQL is not visible from web browsers since it's encryted through a server side macro. Such macros are a feature unique to Clojure and other [Lisps](https://en.wikipedia.org/wiki/Lisp_(programming_language)#List_structure_of_program_code.3B_exploitation_by_macros_and_compilers).




Client side Neo4j Cypher
------------------------

    (go
        (.log js/console (str (<! (neo4j "START x = node(11) RETURN x" {} )))))

Please note that the raw Cypher code is not visible from web browsers as it is encryted via a server side macro, like the SQL calls are.




How secure client side SQL works
--------------------------------

It may seem strange that you can call SQL synchronously from the client yet the call is sent to the server, is secure, and behaves asynchronous internally. Welcome to the world of Lisp!

To understand a bit more about this you need to realise that Clojure is an implementation of Lisp for the JVM, and Clojurescript is Lisp on Javascript. Lisp itself has alot of special features which are not available in other languages, such as the ability to write code itself, also known as Lisp Macros. This ability is not available as a first class feature in any other language!

Before I stray too much away from the point, there are two parts of the Clojure implementation of Lisp that allows synchronous secure client-side SQL/Cypher:

- core.async
- macros (no, not your C++ macros)

When you make a client side SQL/Cypher call it is encyrpted using a Macro at compile time:

    (defmacro sql [sql-str params]
        `(webapp.framework.client.coreclient.sql-fn
            ~(encrypt sql-str)
            ~params))

This means that the string in a client side call...

    (go
        (.log js/console (str (<! (sql "SELECT * FROM test_table where name = ?" ["shopping"] )))))

... will be rewritten at compile time, making it impossible for anyone who does "View source" on your web page to see the SQL code!




Functions available
-------------------------------------------------------
There are many library functions available, although it is a bit of a mess having to :require and :use everything in the Clojurescript namespace header:

    (ns webapp.client.views.loginpanel
        (:refer-clojure :exclude [val empty remove find next parents])
        (:require
            [cljs.reader :as reader]
            [crate.core :as crate]
            [cljs.core.async :as async :refer [chan close!]]
            [clojure.string]
        )
        (:use
            [webapp.framework.client.coreclient :only [body-html new-dom-id debug popup hide-popovers
                                                       show-popover set-text value-of find-el sql-fn neo4j-fn
                                                       swap-section el clear remote  add-to on-mouseover-fn on-click-fn]]
            [jayq.core                          :only [$ css  append fade-out fade-in empty attr bind]]
            [webapp.framework.client.help       :only [help]]
            [webapp.framework.client.eventbus   :only [do-action esb undefine-action]]
            [domina                             :only [ by-id value destroy! ]]
      )
      (:require-macros
        [cljs.core.async.macros :refer [go alt!]])
      (:use-macros
            [webapp.framework.client.eventbus :only [redefine-action define-action]]
            [webapp.framework.client.coreclient :only [ns-coils makeit defn-html on-click on-mouseover sql defn-html defn-html2 neo4j]]
            [webapp.framework.client.interpreter :only [! !! !!!]]
         )
    )
    (ns-coils 'webapp.client.views.loginpanel)




**show-popover** - Shows a Bootstrap 3 style popover above an element

    (show-popover   "email-input"
                    "Email can not be blank")

    (show-popover   "password-input"
                    "Password is incorrect"
                    {:placement "top"})



**swap-section** - Swaps a HTML element with a new element

    (swap-section   "my-section"
                    "<div>Replace the element with ID my-section" with this text</div>")



**do-action** - Puts a message onto the service bus. This can be picked up by zero or more receivers

    (do-action  "say-hello")



**define-action** - Acts on a message sent to the service bus

    (define-action  "say-hello"
      (.log js/console "Hello"))



**do-action message** - Puts a message onto the service bus with paramters. This can be picked up by zero or more receivers

    (do-action  "say-hello" {:name "Peter"})




**define-action** - Acts on a message sent to the service bus. Please note that the message is an implicitly defined variable in an action

    (define-action  "say-hello"
      (.log js/console (str "Hello" (:name message))))





**sql** - Calls the server and executes SQL and returns it to the client

    (go
         (let [
                 search-db-for-user   (<! (sql "SELECT * FROM users where user_name = ?"
                                      [username] ))
                 user-already-exists  (pos? (count search-db-for-user))
              ]
                 (if user-already-exists ...

**set-text** - Set the text of an element

    (set-text "message-element" "You must enter an email address")




Deploying an application to a Java server
-------------------------------------------------------

    mkdir srcprod && cd srcprod
    mkdir webapp_config && cd webapp_config

    touch settings.clj
    ... copy and amend the settings from coils/srcdev/webapp_config/settings.clj ...

    cd ../../coils

    lein with-profile prod cljsbuild clean
    lein with-profile prod cljsbuild once

    lein with-profile prod ring uberwar
    ... deploy the resulting war file ...

