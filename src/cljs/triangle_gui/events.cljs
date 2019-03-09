(ns triangle-gui.events
  (:require
   [re-frame.core :as re-frame]
   [triangle-gui.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))


(defn handle-next-turn                          ;; maybe choose a better name like `delete-item`
  [coeffects event]                               ;; `coeffects` holds the current state of the world.
  (let [pegs (second event)                    ;; extract id from event vector
        db      (:db coeffects)]                  ;; extract the current application state
    (js/console.log pegs db event)
    {:db (assoc db :pegs-remaining (dec pegs))}

    ))

(re-frame/reg-event-fx
  :next-turn
  handle-next-turn
  )


(defn set-peg
  [coeffects event]
  (let [i (second event)                    ;; extract id from event vector
        db (:db coeffects)]               ;; extract the current application state
    (js/console.log i )
    {:db db}
    ))

(re-frame/reg-event-fx
  :set-open-peg
  set-peg
  )