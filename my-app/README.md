Skriv kode i Test.NICE

Generér parser og scanner når du står i /my-app/src/coco:
java -jar Coco.jar NICE32.atg -package "src.coco"

Kør cd ../.. for at hoppe ud i /my-app

Kompilér java filer når du står i /my-app:
javac -d out src/Main.java src/abstract_syntax/\*.java src/coco/\*.java (fjern black-slashes)

Kør main med input når du står i /my-app:
java -cp out src.Main src/Test.NICE

Nu burde du have fået spyttet et AST ud i tekstform i terminalen.
