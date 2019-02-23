(ns triangle-gui.db)


;Starting Application State
;game is initialized to a default for display purposes - then set by the user
;pegs-remaining is the number of pegs on the board, starting at 14. Proxy for turn number
(def default-db
  {:name "re-frame"
   :pegs-remaining 14})
