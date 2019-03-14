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
;make moves - gets all potential game boards
(defn get-moves-samerow "given a hole, row, and game board, returns all moves on the same row. Moves are returned as possible game boards"
  [hole row game]
  (if (< row 2)
    '[]
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
                                                              (subvec game (+ row 1))))))))))))))

(defn get-moves-above "given a hole, row, and game board, returns all moves from above the row. Moves are returned as possible game boards"
  [hole row game]
  (if (< row 2)
    '[]          ; there are no moves above in the first or second rows
      ;TODO write function that combines this and does the below ones as well... would save a lot of space. pass da function
      (let
        [ left-move (loop [r row
                            index (which-element-in-row hole)
                            move '[]]
                       (if (or (< r 0) (< index 0) (= (count move) 3))
                         move
                         (recur (- r 1) (- index 1) (conj move (which-element-in-game r index)))))
         right-move (loop [r row
                           index (which-element-in-row hole)
                           move '[]]
                        (if (or
                              (< r 0)
                              (>= r (count game))
                              (>= index (count (nth game r)))
                              (= (count move) 3))
                          move
                          (recur (- r 1) index (conj move (which-element-in-game r index)))))
          movelist (filter #(= 3 (count %)) (list right-move left-move))]
          (loop [moves (filter #(= 2 (reduce + (let [g (into '[] (map js/parseInt (flatten game)))]
                                               (list
                                                 (nth g (first %))
                                                 (nth g (second %))
                                                 (nth g (nth % 2))))))
                                                  movelist)
                 new_games []]
            (if (zero? 0)
              new_games
              (recur (rest moves) (conj new_games (split-tree (into []
                                                                      (assoc
                                                                        (assoc (assoc (vec (flatten game)) (first (first moves)) 1) (second (first moves)) 0)
                                                                        (nth (first moves) 2) 0)
                                                                      )))))))))

(defn get-moves-below "given a hole, row, and game board, returns all moves from below the row.  Moves are returned as possible game boards"
  [hole row game]
  (if (> row 2)
    '[]  ; there are no moves in the last 2 rows going down.s
      ;TODO write function that combines this and does the above ones as well... would save a lot of space. pass da function
    (let [left-move (loop [r row
                            index (which-element-in-row hole)
                            move '[]]
                       (if (or (< r 0) (< index 0) (= (count move) 3))
                         move
                         (recur (+ r 1) index (conj move (which-element-in-game r index)))))
          right-move (loop [r row
                             index (which-element-in-row hole)
                             move '[]]
                        (if (or (< r 0)
                                (>= r (count game))
                                (>= index (count (nth game r)))
                                (= (count move) 3))
                          move
                          (recur (+ r 1) (+ index 1) (conj move (which-element-in-game r index)))))
          movelist (filter #(= 3 (count %)) (list right-move left-move))
          ]
      (loop [moves (filter #(= 2 (reduce +
                                         (let [g (into '[] (map js/parseInt (flatten game)))]
                                           (list
                                             (nth g (first %))
                                             (nth g (second %))
                                             (nth g (nth % 2))))))
                           movelist)
             new_games []]
        (if (zero? 0)
          new_games
          (recur (rest moves) (conj new_games (split-tree (into []
                                                                  (assoc
                                                                    (assoc (assoc (vec (flatten game)) (first (first moves)) 1) (second (first moves)) 0)
                                                                    (nth (first moves) 2) 0)
                                                                  )))))))))



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