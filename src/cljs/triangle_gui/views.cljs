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
(defn main-interface []
  [:div  {:style {:background-color "#FFFFFF" :height "75%" :width "100%"}} [:div "Main Game Interface ... "] [:div "Main Game Interface ... "]]

  )
(defn main-panel []
  [:div {:style {:background-color "#009688" :width "100%" :height "75%" :margin 0}}
   [title-bar]
   [main-interface]
   [control-panel]
   ]
  )
