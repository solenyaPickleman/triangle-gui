(ns triangle-gui.events
  (:require
   [re-frame.core :as re-frame]
   [triangle-gui.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;;supporting functions for handle next turn
(defn which-row "given a peg index, return integer corresponding to row 1-4"
  [i]
  (cond
    (< i 1) 0
    (< i 3) 1
    (< i 6) 2
    (< i 10) 3
    :else 4)
  )
;this section builds a  game
(defn get-peg "gets a peg or space" [is-peg]
  (if is-peg 0 1))

(defn split-tree "takes a list and builds it into the game board format"
  [l]
  [(subvec l 0 1) (subvec l 1 3) (subvec l 3 6) (subvec l 6 10) (subvec l 10 15)]
  )
(defn which-element-in-row "given a peg index, return position in row"
  [i]
  (cond
    (< i 1) (- i 0)
    (< i 3) (- i 1)
    (< i 6) (- i 3)
    (< i 10) (- i 6)
    :else (- i 10)
    )
  )
(defn which-element-in-game "given row and index, return overall index of peg in game"
  [row index]
  (+ index (cond
             (= row 0) 0
             (= row 1) 1
             (= row 2) 3
             (= row 3) 6
             :else 10
             ))
  )

(defn get-moves-samerow "given a hole, row, and game board, returns all moves on the same row. Moves are returned as possible game boards"
  [hole row game]
  (if (< row 2)
    '[]
    (do
      (let
        [left-move (if (< (which-element-in-row hole) 2) '[] (subvec (nth game row) (- (which-element-in-row hole) 2) (+ 1 (which-element-in-row hole))))
         right-move (if (> (+ 2 (which-element-in-row hole)) row) '[] (subvec (nth game row) (which-element-in-row hole) (+ 3 (which-element-in-row hole))))
         movelist (map #(and (= 2 (reduce + %)) (= 3 (count %))) (list left-move right-move))
         ]
        ;replace current row with possible future rows
        ;cast to string, substring replace with all possible options
        (loop [moves movelist
               left true
               new_games []]
          (if (empty? moves)
            (into [] (filter #(not (nil? %)) new_games))
            (recur
              (subvec moves 1)
              false
              (conj new_games (if (not (first moves))
                                nil
                                (split-tree (into [] (flatten (conj
                                                                (subvec game 0 row)
                                                                (let [r (nth game row)
                                                                      start (if left (- (which-element-in-row hole) 2) (which-element-in-row hole))
                                                                      end (if left (which-element-in-row hole) (+ 3 (which-element-in-row hole)))
                                                                      ]
                                                                  (into [] (flatten (conj
                                                                                      (subvec r 0 start)
                                                                                      (into [] (map #(if (zero? %) 1 0) (subvec r start end)))
                                                                                      (subvec r end)))))
                                                                (subvec game (+ row 1)))))))))))))))

(defn handle-next-turn
  [coeffects event]                               ;; co-effects holds the current state of the world.
  (let [pegs (second event)                    ;; extract # of pegs from event vector
        db      (:db coeffects)]                  ;; extract the current application state
    (js/console.log pegs db event )
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
    {:db (assoc db :game (split-tree (into [] (map #(get-peg (= % i)) (range 15)))))}
    ))

(re-frame/reg-event-fx
  :set-open-peg
  set-peg
  )