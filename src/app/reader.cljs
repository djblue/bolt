(ns app.reader
  (:require [cljs-css-modules.macro :refer-macros [defstyle]]
            [reagent.core :as r]
            [app.nav-panel :refer [nav-panel]]
            [app.page :refer [page]]))

(defstyle style
  [:.reader {:height "calc(100vh - 61px)"
             :display "flex"
             :flex-direction "row"
             :justify-content "space-between"}]
  [:.nav-container {:display "flex"
                    :position "relative"
                    :width "320px"}]
  [:.page-container {:display "flex"
                     :overflow "scroll"
                     :flex "1"}]
  [:.scroll-box {:position "absolute"
                 :top 0
                 :right 0
                 :left 0
                 :bottom 0
                 :overflow-y "scroll"}])

(defn reader []
  (let [!ref (atom nil)]
    (r/create-class
     {:display-name "reader"
      :reagent-render
      (fn [{:keys [file nav-open? on-prev on-next on-set-page]}]
        (let [{:keys [id page-stack pages]} file
              page-stack (if (empty? page-stack) '(0) page-stack)
              top (first page-stack)
              has-more? (< top (dec (count pages)))]
          [:div {:class (:reader style)}
           (if nav-open?
             [:div {:class (:nav-container style)}
              [:div {:class (:scroll-box style)}
               [nav-panel {:pages pages
                           :selected (first page-stack)
                           :on-select on-set-page}]]])
           [:div {:ref (fn [com] (reset! !ref com))
                  :class (:page-container style)}
            [page {:src (str "api/books/" id "/pages/" top "?width=960")
                   :on-prev on-prev
                   :on-next #(do
                               (set! (.-scrollTop @!ref) 0)
                               (on-next))}]
            (if has-more? ;; load next image to cache image
              [:img
               {:style {:display :none}
                :src (str "api/books/" id "/pages/" (inc top) "?width=960")}])]]))})))
