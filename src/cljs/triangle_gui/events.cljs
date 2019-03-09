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

;this section builds a  game
(defn get-peg "gets a peg or space" [is-peg]
  (if is-peg 0 1))

(defn split-tree "takes a list and builds it into the game board format"
  [ l]
  [(subvec l 0 1)
   (subvec l 1 3)
   (subvec l 3 6)
   (subvec l 6 10)
   (subvec l 10 15)
   ]
  )

(defn set-peg
  [coeffects event]
  (let [i (second event)                    ;; extract id from event vector
        db (:db coeffects)]               ;; extract the current application state
    (js/console.log i (split-tree (into [] (map #(get-peg (= % i)) (range 15)))))
    {:db (assoc db :game (split-tree (into [] (map #(get-peg (= % i)) (range 15)))))}
    ))

(re-frame/reg-event-fx
  :set-open-peg
  set-peg
  )