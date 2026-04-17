# Used to run tests. (See main makefile for tutorial.)

#Compiler variable
CXX = g++

#Compiler flag variables
DEBUG_FLAGS = -ggdb -O0

RELEASE_FLAGS = -O2 -DNDEBUG

WARNINGS = -pedantic-errors -Wall -Wextra -Weffc++ -Wconversion -Wsign-conversion -Werror

LANGUAGE = -std=c++26

CXXFLAGS = ${DEBUG_FLAGS} ${WARNINGS} ${LANGUAGE}

#Compilation targets
test: testEx.o
	${CXX} ${CXXFLAGS} $^ -o $@

testEx.o: testEx.cpp
	${CXX} ${CXXFLAGS} -c $^ -o $@