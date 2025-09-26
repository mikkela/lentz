cmake_minimum_required(VERSION 3.22)
project(lentz LANGUAGES CXX VERSION 0.1.0)

# Standard & policies
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Options
option(LENTZ_BUILD_TESTS "Build unit tests" ON)
option(LENTZ_WARNINGS_AS_ERRORS "Treat warnings as errors" OFF)
option(LENTZ_ENABLE_SANITIZERS "Enable sanitizers in debug" ON)

# Warnings helper target
add_library(lentz_warnings INTERFACE)
if (MSVC)
    target_compile_options(lentz_warnings INTERFACE /W4 $<$<BOOL:${LENTZ_WARNINGS_AS_ERRORS}>:/WX>)
else()
    target_compile_options(lentz_warnings INTERFACE -Wall -Wextra -Wpedantic -Wconversion
            $<$<BOOL:${LENTZ_WARNINGS_AS_ERRORS}>:-Werror>)
endif()

# Sanitizers (kun Debug)
if (LENTZ_ENABLE_SANITIZERS AND CMAKE_BUILD_TYPE STREQUAL "Debug" AND NOT MSVC)
    add_library(lentz_sanitizers INTERFACE
            include/lentz/ast/Nodes.hpp
            src/ast/Nodes.cpp
            src/cli/lentzc.cpp
            include/lentz/util/Diagnosis.hpp
            tests/unit/minitest.hpp
            tests/unit/parser/helpers.hpp
            include/lentz/frontend/Frontend.hpp
            tests/unit/test_core.cpp
            src/analysis/Symbols.cpp
            include/lentz/analysis/Symbols.hpp
            include/lentz/backend/Emitter.hpp
            src/backend/cpp/Emitter.cpp
            src/runtime/Runtime.cpp)
    target_compile_options(lentz_sanitizers INTERFACE -fsanitize=address,undefined)
    target_link_options(lentz_sanitizers INTERFACE -fsanitize=address,undefined)
endif()

# Underprojekter
add_subdirectory(src)

if (LENTZ_BUILD_TESTS)
    include(CTest)          # aktiverer 'ctest'
    enable_testing()
    add_subdirectory(tests)
endif()
