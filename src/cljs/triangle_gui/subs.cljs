(ns triangle-gui.subs
  (:require
   [re-frame.core :as re-frame]))

;get name from db
(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(defn query-fn
  [db v]         ;; db is current app state, v the query vector
  (:pegs-remaining db))   ;; not much of a materialised view

;get the number of pegs remaining on the board from db.. maybe replace with (reduce + game)
(re-frame/reg-sub
  ::pegs-remaining
  (fn [db]
    (:pegs-remaining db)))

;get the current game from the app state
(re-frame/reg-sub
  ::game
  (fn [db]
    (:game db)))