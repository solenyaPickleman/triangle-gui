(ns triangle-gui.views
  (:require
   [re-frame.core :as re-frame]
   [triangle-gui.subs :as subs]
   [triangle-gui.events :as events]
   ))

;defs a control element with buttons to start a new game, move forward and move back (re-frame.core/dispatch [:delete-item item-id])
(defn control-panel []
  (let [peg @(re-frame/subscribe [::subs/pegs-remaining])]
    [:div {:class "control-panel" :style {:width "100%"}}
     [:input
        {:type "button" :on-click #(re-frame.core/dispatch [::events/initialize-db] )
         :style {:width "10%" :margin "10px" :margin-left "40%" } :value "New Game"}]
     [:input
        {:type "button" :on-click #(re-frame.core/dispatch [:next-turn peg])
          :style {:width "10%" :margin "10px"} :value "Next Turn"}]
     ]))
(defn title-bar []
  [:div {:style {:width "100%" :height "15%" }}
                [:div
                 [:h1  {:style {:margin-left "40%"  :color "#FFFFFF"}}
                  "One Peg Man"]  ]
                [:div
                 [:span {:style {:margin-left "33%"  :color "#B2DFDB"}}
                  "Beat the Cracker Barrel Triangle Game ... with one try"]   ]
                ])


;build image .. either an empty space or a space filled with a peg
(defn get-image [data imageindex]

  (loop [images [] d data ]
    (if (= (count images) (count data))
      images
      (let [i (swap! imageindex inc)]
      (recur (conj images (if (= (first d) 0)
                             [:img {:on-click #(re-frame.core/dispatch [:set-open-peg i]) :src "wdotempty.png" :alt "broken dot" :height "10%" :width "10%"}]
                             [:img {:on-click #(re-frame.core/dispatch [:set-open-peg i]) :src "wdot.png" :alt "broken dot" :height "10%" :width "10%"}]
                             ))
             (rest d)
             ))))
  )

;build the game board.. define if
(defn build-game [game]
  (let [keyindex (atom 0)  imageindex (atom -1)]
    (->> game
         (map #(get-image % imageindex))
         (map #(into [:div
                      {:key (swap! keyindex inc) :style
                            {:margin-left  (clojure.string/join "" [(str (- 40 (* 5 (dec (count %))))) "%"])
                             :margin-right (clojure.string/join "" ["-" (str (- 40 (* 5 (dec (count %))))) "%"])}
                       }] %))
         ))
  )

(defn main-interface []
  (let [game @(re-frame/subscribe [::subs/game])]
    [:div {:style {:background-color "whitesmoke" :height "75%" :width "100%"}}
     [:div
      (build-game game)
      ]]
    )
  )

(defn main-panel []
  [:div {:style {:background-color "#009688" :width "100%" :height "75%" :margin 0}}
   [title-bar]
   [main-interface]
   [control-panel]
   ]
  )
