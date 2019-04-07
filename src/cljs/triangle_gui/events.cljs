(ns triangle-gui.events
  (:require
   [re-frame.core :as re-frame]
   [triangle-gui.db :as db]
   [clojure.zip :as zip]
   [clojure.data.zip :as zipfunc]
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
      [left-move (if (< (which-element-in-row hole) 2)
          (nth game row)
          (vec (concat
            (subvec  (nth game row) 0 (- (which-element-in-row hole) 2))
            (map #(if (zero? %) 1 0) (subvec  (nth game row) (- (which-element-in-row hole) 2)  (+ 1 (which-element-in-row hole))))
            (subvec  (nth game row) (+ 1 (which-element-in-row hole))))))
       right-move (if (>= (+ 2 (which-element-in-row hole)) row)
                    (nth game row)
                    (vec (concat
                      (subvec  (nth game row) 0 (which-element-in-row hole))
                      (map #(if (zero? %) 1 0) (subvec  (nth game row) (which-element-in-row hole) (+ 3 (which-element-in-row hole))))
                      (subvec  (nth game row) (+ 3 (which-element-in-row hole))))))
       movelist (filter #(<  (reduce + %) (reduce + (nth game row)))  (list left-move right-move))
       ]
      ;replace current row with future rows
      (loop [moves movelist  new_games []]
        (if (empty? moves)
          new_games
          (recur
            (subvec (vec moves) 1)
            (conj new_games
                    (if ( = row 4)
                      (into [] (concat (subvec game 0 row) [(nth moves 0)]))
                      [(subvec game 0 row) (vec (first moves)) (subvec game (inc row))]
                  ))))))))

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
            (if (empty? moves)
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
        (if (empty? moves)
          new_games
          (recur (rest moves) (conj new_games (split-tree (into []
                                                                  (assoc
                                                                    (assoc (assoc (vec (flatten game)) (first (first moves)) 1) (second (first moves)) 0)
                                                                    (nth (first moves) 2) 0)
                                                                  )))))))))

(defn get-moves-for-hole "given a game board and specific hole, give all possible moves for that peg"
  [hole game]
  (into  [] (concat
    (get-moves-above hole (which-row hole) (split-tree  (vec (flatten game))))
    (get-moves-samerow hole (which-row hole) (split-tree (vec (flatten game))))
    (get-moves-below hole (which-row hole) (split-tree (vec (flatten game))))
    )))

(defn get-moves "given a game board, gives all possible moves in the form of the potential game board"
  [game]
   (loop [index 0 moves []]
     (if (= index (count (flatten game)))
       (vec moves)                                           ;at this point ,all have been looped through so return the vector of potential game boards
       (recur
         (+ index 1)
         (if (= (nth (flatten game) index) 1)
           moves
           (into [] (concat moves (get-moves-for-hole index game)))
           )))))

(defn branch "zipper branching function"
  [game]
  (and (some? (map #(= 1 (reduce + (flatten %))) (get-moves game)))
       (some? (map #(  > (reduce + (flatten game)) (reduce + (flatten  %))  ) (get-moves game)))


       ))

(defn beat-game "take a game, return the solved state"
  [game]
  ;(let [tree (filter #(clojure.zip/end? %) (zipfunc/descendants (clojure.zip/zipper branch get-moves clojure.zip/make-node game)))]
  ;  (print tree)
  ;  tree))


  (loop [tree (zipfunc/descendants (clojure.zip/zipper branch get-moves clojure.zip/make-node game))]
    (let [x (next tree)]
    (if (= 3 (reduce + (flatten (next tree))))
      (do (print (zip/node tree))
        (zip/node tree))
      (recur (next tree))
      )
    )))



(defn handle-next-turn
  [coeffects event]                               ;; co-effects holds the current state of the world.
  (let [pegs (second event)                    ;; extract # of pegs from event vector
        db      (:db coeffects)]                  ;; extract the current application state
    (print (beat-game (:game db)) )
    {:db (assoc db :test (beat-game (:game db)))}
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