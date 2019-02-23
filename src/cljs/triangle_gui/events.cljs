(ns triangle-gui.events
  (:require
   [re-frame.core :as re-frame]
   [triangle-gui.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
