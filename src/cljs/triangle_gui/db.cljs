(ns triangle-gui.db)

;Starting Application State
;game is initialized to a default for display purposes - then set by the user
;pegs-remaining is the number of pegs on the board, starting at 14. Proxy for turn number ..
;TODO think about replacing pegs-remaining with (reduce + game)
(def default-db
  {:name "re-frame"
   :game [[1] [1 1] [1 0 1] [1 1 1 1] [1 1 1 1 1]]
   :pegs-remaining 14})
