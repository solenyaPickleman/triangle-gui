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
                             [:img {:on-click #(re-frame.core/dispatch [:set-open-peg i]) :src "empty.png" :alt "broken dot" :height "10%" :width "10%"}]
                             [:img {:on-click #(re-frame.core/dispatch [:set-open-peg i]) :src "dot.png" :alt "broken dot" :height "10%" :width "10%"}]
                             ))
             (rest d)
             ))))
  )

;build the game board.. define if
(defn build-game [game]
  (let [keyindex (atom 0)  imageindex (atom -1)]
    (as-> game g
         (map #(get-image % imageindex) g)
          (map #(into [:div {:key (swap! keyindex inc) :style { :display "flex" :justify-content "center" :margin-bottom "-3%" :transform "scale(0.7, 0.7)"} }] %) g)
          (into '[] g)
          ))
  )

(defn main-interface []
  (let [game @(re-frame/subscribe [::subs/game])]
    [:div {:style {:background-color "whitesmoke" :height "75%" :width "100%"}}
     [:div
      (let [data (build-game game)]
        ;(vec (butlast data))
        (sort-by #(get-in % [1 :key]) <  (conj  (butlast data) (assoc-in (last data) [1 :style :margin-bottom] "0%") ))
        )
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
