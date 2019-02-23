# triangle-gui

A [re-frame](https://github.com/Day8/re-frame) application designed to guide a user through solving the infamous game of games:

The Cracker Barrell peg game that is in the shape of a triangle. 

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
