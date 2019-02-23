(ns triangle-gui.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(defn query-fn
  [db v]         ;; db is current app state, v the query vector
  (:pegs-remaining db))   ;; not much of a materialised view

(re-frame/reg-sub
  ::pegs-remaining
  (fn [db]
    (:pegs-remaining db)))