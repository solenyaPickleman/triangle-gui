(ns triangle-gui.views
  (:require
   [re-frame.core :as re-frame]
   [triangle-gui.subs :as subs]
   [triangle-gui.events :as events]
   ))

;defs a control element with buttons to start a new game, move forward and move back (re-frame.core/dispatch [:delete-item item-id])
(defn control-panel []
  (let [peg @(re-frame/subscribe [::subs/pegs-remaining])]
    [:div {:class "control-panel"}
     [:input {:type "button" :on-click #(re-frame.core/dispatch [::events/initialize-db] ) :style {:width "10%" :margin "10px"} :value "New Game"}]
     [:input {:type "button" :on-click #(re-frame.core/dispatch [:next-turn peg]) :style {:width "10%" :margin "10px"} :value "Next Turn"}]
     ]


    )
  )

(defn main-panel []
    [:div
     [:span "Goodbye javascript hello clojure"]

     [control-panel ]

     ]

  )
