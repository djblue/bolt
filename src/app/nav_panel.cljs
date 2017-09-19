(ns app.nav-panel
  (:require [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.panel {:width "100%"
            :background "#354458"
            :color "#E9E0D6"
            :box-shadow "inset -3px 0 10px rgba(0,0,0,0.5)"
            :min-height "calc(100vh - 61px)"
            :user-select "none"}
   [:.item {:padding "10px"
            :box-sizing "border-box"
            :border-left "3px solid #354458"
            :transition "background 0.25s"
            :cursor "pointer"}
    [:&:hover {:background "rgba(0,0,0,0.5)"
               :border-left "3px solid rgba(0,0,0,0.5)"}]]
   [:.selected {:background "rgba(0,0,0,0.5)"
                :border-left "3px solid #29ABA4"}]])

(defn nav-panel [{:keys [pages selected on-select]}]
  [:div
   {:class (:panel style)}
   (map-indexed
    (fn [idx itm]
      [:div {:key idx
             :class (if (= idx selected) "item selected" "item")
             :on-click #(on-select idx)} itm])
    pages)])
