(ns triangle-gui.events
  (:require
  [re-frame.core :as re-frame]
  [triangle-gui.db :as db]
  ))



(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;this section builds a  game
(defn get-peg "gets a peg or space" [is-peg]
  (if is-peg 0 1))

(defn split-tree "takes a list and builds it into the game board format"
  [l]
  [(subvec l 0 1) (subvec l 1 3) (subvec l 3 6) (subvec l 6 10) (subvec l 10 15)]
  )

(defn handle-next-turn
  [coeffects event]                               ;; co-effects holds the current state of the world.
  (let [pegs (second event)                    ;; extract # of pegs from event vector
        db      (:db coeffects)]                  ;; extract the current application state
    {:db (assoc db :test "t")}
    ))

(re-frame/reg-event-fx
  :next-turn
  handle-next-turn
  )

(defn set-peg
  [coeffects event]
  (let [i (second event)                    ;; extract id from event vector
        db (:db coeffects)]               ;; extract the current application state
    {:db (assoc db :game (split-tree (into [] (map #(get-peg (= % i)) (range 15)))))}
    ))

(re-frame/reg-event-fx
  :set-open-peg
  set-peg
  )