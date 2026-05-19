; ESP compiler setup
.section .text
.global app_main
.literal_position


; B0
app_main:
PORT_SETUP DIGITAL OUTPUT2 ; PORT_SETUP DIGITAL OUTPUT 2
XORB       b0,  b0,   b0 ; b0 := false

; B1
.Lloop:                  ; Lloop:
ORB        b1,  b0,   b0 ; b1 := b0
COMPR/W     b0    2 1000 ; COMPW b0 2 1000
J         .Lloop         ; GOTO Lloop

