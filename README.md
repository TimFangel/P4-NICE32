Skriv kode i *Test.NICE*

Generér parser og scanner når du står i */my-app/src/frontend/coco*:
`java -jar Coco.jar NICE32.atg -package "frontend.coco"`

Kør `cd ../..` for at hoppe ud i */my-app*

Kompilér java filer når du står i */my-app*:
`javac -d out src/frontend/Main.java src/frontend/abstract_syntax/*.java src/frontend/coco/*.java src/frontend/semantic_analysis/*.java src/frontend/symboltable/*.java`

Kør main med input når du står i */my-app*:
`java -cp out frontend.Main src/Test.NICE`

Nu burde du have fået spyttet et AST ud i tekstform i terminalen.
