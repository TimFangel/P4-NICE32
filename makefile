# Used to run main. (Tutorial at the bottom.)

#Compiler variable
CXX = g++

#Compiler flag variables
DEBUG_FLAGS = -ggdb -O0

RELEASE_FLAGS = -O2 -DNDEBUG

WARNINGS = -pedantic-errors -Wall -Wextra -Weffc++ -Wconversion -Wsign-conversion -Werror

LANGUAGE = -std=c++26

CXXFLAGS = ${DEBUG_FLAGS} ${WARNINGS} ${LANGUAGE}

#Compilation targets
main.out: main.o
	${CXX} ${CXXFLAGS} $^ -o $@

main.o: main.cpp
	${CXX} ${CXXFLAGS} -c $^ -o $@



# Kort tutorial. For mere se manual eller https://www.youtube.com/watch?v=U1I5UY_vWXI	\
Opbygning af nedenstående -> target: prerequisites  									\
Dermed skal de opdateres, hvis andre filer skal bruges for at compilere.				\
Eksempler til når man får flere filer.													\
Med nedenstående opbygning med .o targets og											\
deres prerequisites, recompiler den kun 												\
filer, der har ændret sig siden sidste compilering. 									\

# $@ -> giv filen samme navn som target. $^ -> brug prerequisite filerne				\
main.out: hello.o main.o																\
	${CXX} ${CXXFLAGS} $^ -o $@															\
																						\
hello.o: hello.cpp 																		\
	${CXX} ${CXXFLAGS} -c $^ -o $@														\
																						\
main.o: main.cpp																		\
	${CXX} ${CXXFLAGS} -c $^ -o $@														\


# Debug explanation \
ggdb -> debug symbols, O0 -> no optimizations

# Release explanation \
DNDEBUG -> disables assert() macros, O2 -> general optimizations

# Warnings explanation 																			\
pedantic-errors -> conform to C++ standard, 													\
Wall -> basic warnings, Wextra -> extra warnings 												\
Weffc++ -> conform to Effective C++ guidelines (memory, objects, constructors etc.),		   	\
Wconversion -> warn about implicit type conversion with loss of data,							\
Wsign-conversion -> signed/unsigned conversions, 												\
Werror -> treat warnings as errors (maybe disable?)