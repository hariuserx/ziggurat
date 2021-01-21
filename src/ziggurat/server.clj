(ns ziggurat.server
  (:require [clojure.tools.logging :as log]
            [cheshire.generate :refer [add-encoder encode-str]]
            [mount.core :as mount :refer [defstate]]
            [org.httpkit.server :as kit]
            [ziggurat.config :refer [ziggurat-config]]
            [ziggurat.server.routes :as routes])
  (:import (java.time Instant)
           (org.httpkit.server HttpServer)))

(add-encoder Instant encode-str)

(defn- start [handler]
  (let [conf         (:http-server (ziggurat-config))
        port         (:port conf)
        thread-count (:thread-count conf)]
    (log/info "Starting server on port:" port)
    (kit/run-server handler {:port   port
                             :thread thread-count})))

(defn- stop [^HttpServer server]
  (kit/server-stop! server)
  (log/info "Stopped server"))

(defstate server
          :start (start (routes/handler (:actor-routes (mount/args))))
          :stop (stop server))
