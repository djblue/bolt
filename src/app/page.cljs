(ns app.page
  (:require [cljs-css-modules.macro :refer-macros [defstyle]]
            [garden.stylesheet :refer [at-media]]
            [reagent.core :as r]))

(def css-transition-group
  (r/adapt-react-class js/React.addons.CSSTransitionGroup))

(defstyle style
  [:.img {:display :block
          :user-select :none
          :width "100%"
          :max-width "100%"
          :box-shadow "0 0 10px rgba(0,0,0,0.5)"}]
  [:.page {:width "100%"}
   (at-media {:max-width "960px"}
             [:.img  {:min-width "100vw"}])
   [:.fade-enter {:position :absolute
                  :z-index 100}]
   [:.fade-leave {:position :absolute
                  :z-index 200
                  :top 0
                  :opacity 1}]
   [:.fade-leave.fade-leave-active {:opacity 0
                                    :transition "opacity 0.2s ease-out"}]
   [:.background {:max-width "960px"
                  :margin "0 auto"
                  :position :relative
                  :min-height "clac(100vh - 61px)"}]
   [:.container {:position :relative
                 :margin "0 auto"}
    [:.prev
     :.next {:position :absolute
             :z-index 100
             :top 0
             :bottom 0
             :cursor :pointer}]
    [:.prev {:left 0
             :right "65%"}]
    [:.next {:right 0
             :left "65%"}]]])

(defn img [{:keys [src]}]
  [css-transition-group {:transition-name :fade
                         :transition-enter-timeout  200
                         :transition-leave-timeout  200}
   [:img.img {:key src
              :class (style :img)
              :src src}]])

(defn page [{:keys [src on-prev on-next]}]
  [:div {:class (:page style)}
   [:div {:class :container}
    [:div {:class :prev
           :on-click (fn [_] (on-prev))}]
    [:div {:class :next
           :on-click (fn [_] (on-next))}]
    [:div {:class :background}
     [img {:src src}]]]])
