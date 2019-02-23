(ns triangle-gui.views
  (:require
   [re-frame.core :as re-frame]
   [triangle-gui.subs :as subs]
   ))



(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " name]
     [:span "Goodbye javascript hello clojure"]
     ]))
