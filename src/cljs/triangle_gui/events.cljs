(ns triangle-gui.events
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
  [re-frame.core :as re-frame]
  [triangle-gui.db :as db]
  [cljs-http.client :as http]
  [cljs.core.async :as async]
  ))

(defn process-solutions [ solutions]
      (js/console.log "made call")
      (re-frame.core/dispatch [:set-open-peg-solution (clojure.string/split  (:body solutions) "," )])
      )


(defn make-remote-call [game]
      (js/console.log "making call")
      (go (process-solutions  (async/<! (http/get (str "http://solvethepeggame.com/api/" (clojure.string/join "" (flatten game))){:with-credentials? false})))))


(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
     (make-remote-call (:game db/default-db))
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
        db      (:db coeffects)]
       {:db (assoc db :pegs-remaining (dec (:pegs-remaining db)) :game (split-tree (into [] (map js/parseInt (clojure.string/split (nth (:solutions db)  (- 15 (:pegs-remaining db))) "" )))))}
    ))

(re-frame/reg-event-fx
  :next-turn
  handle-next-turn
  )



(defn set-peg
  [coeffects event]
  (let [i (second event)                    ;; extract id from event vector
        db (:db coeffects)]               ;; extract the current application state
    (make-remote-call (split-tree (into [] (map #(get-peg (= % i)) (range 15)))))
    {:db (assoc db :game (split-tree (into [] (map #(get-peg (= % i)) (range 15)))))}
    ))

(re-frame/reg-event-fx
  :set-open-peg
  set-peg
  )


(defn set-solutions
      [coeffects event]                               ;; co-effects holds the current state of the world.
      (let [solutions (second event)                    ;; extract # of pegs from event vector
            db      (:db coeffects)]
           (js/console.log solutions)
           {:db (assoc db :solutions solutions)}
           ))

(re-frame/reg-event-fx
  :set-open-peg-solution
  set-solutions
  )
