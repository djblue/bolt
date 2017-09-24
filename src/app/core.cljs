(ns app.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as r]
            [alandipert.storage-atom :refer [local-storage]]
            [app.nav-bar :refer [nav-bar]]
            [app.gallery :refer [gallery]]
            [app.reader :refer [reader]]
            [cljs.core.async :as a :refer [<! put! chan]]
            [cljs-http.client :as http]))

(enable-console-print!)

(defonce state (local-storage
                (r/atom {:files {}
                         :selected nil
                         :nav-open? false}) :state))

(defn in-range [file n]
  (< -1 n (-> file :pages count)))

(defn set-page! [file n]
  (when (in-range file n)
    (swap! state update-in [:files (:id file) :page-stack] #(cons n %))))

(defn set-prev! [file]
  (let [{:keys [page-stack]} file]
    (set-page! file (dec (first page-stack)))))

(defn set-next! [file]
  (let [{:keys [page-stack]} file]
    (set-page! file (inc (first page-stack)))))

(defn set-undo! [file]
  (when (> (-> file :page-stack count) 0)
    (swap! state update-in [:files (:id file) :page-stack] rest)))

(defn toggle-nav! []
  (swap! state update :nav-open? not))

(defn close! []
  (swap! state assoc :selected nil))

(defn app []
  (let [{:keys [nav-open? selected files]} @state
        file (get files selected)]
    [:div {:style {:min-height "100vh"
                   :background "#E9E0D6"}}
     [nav-bar {:title (or (:title file) "gallery")
               :selected (-> selected nil? not)
               :on-toggle-nav toggle-nav!
               :on-close close!}]
     (if selected
       [reader {:file file
                :nav-open? nav-open?
                :on-prev #(set-prev! file)
                :on-next #(set-next! file)
                :on-set-page #(set-page! file %)}]
       [gallery {:files files
                 :on-select (fn [{:keys [id]}]
                              (go
                                (swap! state assoc :selected id)
                                (swap! state assoc-in [:files id :pages]
                                       (:body (<! (http/get (str "api/books/" id "/pages")))))))}])]))

(defn ^:export render []
  (r/render [app]
            (. js/document (getElementById "root"))))

(defn evt-chan [node evt-key]
  (let [c (chan)]
    (. node (addEventListener (name evt-key) #(put! c %)))
    c))

(defonce keyup (evt-chan js/document :keyup))

(defn get-selected-file []
  (let [state @state]
    (-> state :files (get (-> state :selected)))))

(defn on-keyup [k]
  (when-let [file (get-selected-file)]
    (case k
      "ArrowRight" (set-next! file)
      "l"          (set-next! file)
      "ArrowLeft"  (set-prev! file)
      "h"          (set-prev! file)
      "H"          (set-page! file 0)
      "L"          (set-page! file (-> file :pages count dec))
      "u"          (set-undo! file)
      "Escape"     (close!)
      "q"          (close!)
      "x"          (close!)
      "t"          (toggle-nav!)
      "default")))

(defn normalize [files]
  (->> files
       (map (fn [{:keys [id] :as v}] [id v]))
       (into {})))

(defn do-init []
  (go
    (let [files (normalize (:body (<! (http/get "api/books"))))]
      (swap! state update :files #(merge-with into % files))))
  (go-loop []
    (on-keyup (.-key (<! keyup)))
    (recur))
  (render))

(defonce init (do-init))
