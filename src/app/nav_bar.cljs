(ns app.nav-bar
  (:require [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.icon {:cursor :pointer
           :user-select :none}
   [:.stripe {:width "24px"
              :height "3px"
              :background-color "#354458"
              :border-radius "20px"
              :margin "5px 0"}]]
  [:.bar {:display "flex"
          :justify-content "space-between"
          :background "#29ABA4"
          :align-content "center"
          :position "relative"
          :z-index 200
          :color "#354458"
          :font-weight "bold"
          :text-transform "uppercase"
          :padding "15px 20px"
          :box-sizing "border-box"
          :font-size "24px"
          :border-bottom "3px solid #354458"}
   [:.title {:margin "0 15px"
             :text-overflow :ellipsis
             :white-space :nowrap
             :overflow :hidden}]
   [:.close {:cursor "pointer"
             :font-size "24px"
             :width "28px"
             :height "28px"
             :border-radius "100%"
             :text-align "center"
             :font-weight "bold"
             :user-select "none"}]])

(defn icon [{:keys [on-click]}]
  [:div {:class  (:icon style)
         :on-click on-click}
   [:div.stripe]
   [:div.stripe]
   [:div.stripe]])

(defn nav-bar [{:keys [title selected on-toggle-nav on-close]}]
  [:div {:class (:bar style)}
   [icon {:on-click on-toggle-nav}]
   [:div.title title]
   (if selected
     [:div {:class :close
            :on-click on-close} "\u2715"])])
