; https://juxt.pro/blog/crux-tutorial-datalog

(ns bristol.crux-mercury-assignment
  (:require [crux.api :as crux]))

; 1. make a node
(def node (crux/start-node {}))

; 2. make db out of node
(def db (crux/db node))

(defn easy-ingest
  "Uses Crux put transaction to add a vector of
  documents to a specified system"
  [node docs]
  (crux/submit-tx node
                  (vec (for [doc docs] [:crux.tx/put doc]))))

(def data
  [{:crux.db/id :commodity/Pu
    :common-name "Plutonium"
    :type :element/metal
    :density 19.816
    :radioactive true}

   {:crux.db/id :commodity/N
    :common-name "Nitrogen"
    :type :element/gas
    :density 1.2506
    :radioactive false}

   {:crux.db/id :commodity/CH4
    :common-name "Methane"
    :type :molecule/gas
    :density 0.717
    :radioactive false}

   {:crux.db/id :commodity/Au
    :common-name "Gold"
    :type :element/metal
    :density 19.300
    :radioactive false}

   {:crux.db/id :commodity/C
    :common-name "Carbon"
    :type :element/non-metal
    :density 2.267
    :radioactive false}

   {:crux.db/id :commodity/borax
    :common-name "Borax"
    :IUPAC-name "Sodium tetraborate decahydrate"
    :other-names ["Borax decahydrate" "sodium borate" "sodium tetraborate" "disodium tetraborate"]
    :type :mineral/solid
    :appearance "white solid"
    :density 1.73
    :radioactive false}])

(easy-ingest node data)

;; (def db (crux/db node))

(crux/q (crux/db node)
        '{:find [element]
          :where [[element :type :element/metal]]})

(crux/q db
        '{:find [element]
          :where [[element :type :element/metal]]})

; Example 2
(=
 (crux/q (crux/db node)
         '{:find [element]
           :where [[element :type :element/metal]]})

 (crux/q (crux/db node)
         {:find '[element]
          :where '[[element :type :element/metal]]})

 (crux/q (crux/db node)
         (quote
          {:find [element]
           :where [[element :type :element/metal]]})))

; Example 3
(crux/q (crux/db node)
        '{:find [name]
          :where [[e :type :element/metal]
                  [e :common-name name]]})