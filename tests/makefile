# Used to run tests. (See main makefile for tutorial.)

#Compiler variable
CXX = g++

#Directories
OBJDIR = build

#Compiler flag variables
DEBUG_FLAGS = -ggdb -O0

RELEASE_FLAGS = -O2 -DNDEBUG

WARNINGS = -pedantic-errors -Wall -Wextra -Weffc++ -Wconversion -Wsign-conversion -Werror

LANGUAGE = -std=c++23

CXXFLAGS = ${DEBUG_FLAGS} ${WARNINGS} ${LANGUAGE}

#Sources and objects
SRCS = testEx.cpp   #add more .cpp files here when
OBJS = $(SRCS:%.cpp=$(OBJDIR)/%.o) #rewrites .cpp to .o when compiling

# Target
test: $(OBJS)
	$(CXX) $(CXXFLAGS) $^ -o $@

# Rule to compile .cpp -> build/.o
$(OBJDIR)/%.o: %.cpp | $(OBJDIR)
	$(CXX) $(CXXFLAGS) -c $< -o $@

# Ensure build directory exists
$(OBJDIR):
	mkdir $(OBJDIR)

# Clean
clean:
	rm -rf $(OBJDIR) test