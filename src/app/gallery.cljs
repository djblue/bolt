(ns app.gallery
  (:require [cljs-css-modules.macro :refer-macros [defstyle]]
            [app.page :refer [img]]))

(defstyle style
  [:.gallery {:display "flex"
              :flex-wrap "wrap"
              :justify-content "space-around"
              :max-width "960px"
              :padding "20px"
              :margin "0 auto"}]
  [:.book {:background "#29ABA4"
           :width "200px"
           :margin-right "20px"
           :margin-bottom "20px"
           :box-shadow "2px 2px 10px rgba(0,0,0,0.5)"
           :cursor "pointer"}]
  [:.cover {:display "flex"
            :flex-direction "column"
            :justify-content "space-around"
            :height "300px"
            :overflow "hidden"
            :background "#eee"}]
  [:.title {:text-transform "uppercase"
            :border-top "2px solid #354458"
            :color "#354458"
            :font-weight "bold"
            :padding "20px"}])

(defn gallery [{:keys [files on-select]}]
  (let [display (set (map (fn [[id _]] {:id id :n 0 :width 200}) files))]
    [:div {:class (:gallery style)}
     (->> files
          vals
          (sort-by :title)
          (map-indexed
           (fn [idx {:keys [id title] :as itm}]
             [:div
              {:key idx
               :class (:book style)
               :on-click (fn [_] (on-select itm))}
              [:div {:class (:cover style)}
               [img {:src (str "api/books/" id "/pages/" 0 "?width=200")}]]
              [:div {:class (:title style)} title]])))]))
