(ns ziggurat.server
  (:require [clojure.tools.logging :as log]
            [cheshire.generate :refer [add-encoder encode-str]]
            [mount.core :as mount :refer [defstate]]
            [aleph.http :as http]
            [ziggurat.config :refer [ziggurat-config]]
            [ziggurat.server.routes :as routes])
  (:import (java.time Instant)))

(add-encoder Instant encode-str)

(defn- start [handler]
  (let [conf         (:http-server (ziggurat-config))
        port         (:port conf)
        thread-count (:thread-count conf)]
    (log/info "Starting server on port:" port)
    (http/start-server handler {:port   port})))

(defn- stop [server]
  (.close server)
  (log/info "Stopped server"))

(defstate server
          :start (start (routes/handler (:actor-routes (mount/args))))
          :stop (stop server))
