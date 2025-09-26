# AST
add_library(lentz_ast
        ast/Nodes.cpp
)
target_include_directories(lentz_ast PUBLIC ${PROJECT_SOURCE_DIR}/include)

# RUNTIME
add_library(lentz_runtime
        runtime/Runtime.cpp
)
target_include_directories(lentz_runtime PUBLIC ${PROJECT_SOURCE_DIR}/include)

# FRONTEND (ANTLR + AST)
add_library(lentz_frontend)
file(GLOB LENTZ_ANTLR_GEN "${PROJECT_SOURCE_DIR}/src/frontend/parser/*.cpp")
target_sources(lentz_frontend PRIVATE ${LENTZ_ANTLR_GEN})
target_include_directories(lentz_frontend
        PUBLIC  ${PROJECT_SOURCE_DIR}/include
        PRIVATE ${PROJECT_SOURCE_DIR}/src
        ${PROJECT_SOURCE_DIR}/src/frontend/parser
)
find_package(antlr4-runtime CONFIG REQUIRED)
if(TARGET antlr4_shared)
    set(ANTLR4_RUNTIME_TARGET antlr4_shared)
elseif(TARGET antlr4_static)
    set(ANTLR4_RUNTIME_TARGET antlr4_static)
elseif(TARGET antlr4-runtime)
    set(ANTLR4_RUNTIME_TARGET antlr4-runtime)
elseif(TARGET antlr4::antlr4)
    set(ANTLR4_RUNTIME_TARGET antlr4::antlr4)
else()
    message(FATAL_ERROR "ANTLR4 runtime target not found")
endif()
target_link_libraries(lentz_frontend
        PUBLIC ${ANTLR4_RUNTIME_TARGET}    # propagate ANTLR to users of frontend
        PUBLIC lentz_ast                   # propagate AST
)

# ANALYSIS (bruger AST + evt. runtime)
add_library(lentz_analysis
        analysis/Symbols.cpp
)
target_include_directories(lentz_analysis PUBLIC ${PROJECT_SOURCE_DIR}/include)
target_link_libraries(lentz_analysis
        PUBLIC lentz_ast
        PUBLIC lentz_runtime               # hvis analysis bruger runtime; fjern hvis ikke
)

# BACKEND (bruger analysis; analysis → ast/runtime transitivt)
add_library(lentz_backend
        backend/cpp/Emitter.cpp
)
target_include_directories(lentz_backend PUBLIC ${PROJECT_SOURCE_DIR}/include)
target_link_libraries(lentz_backend
        PUBLIC lentz_analysis
        # PUBLIC lentz_frontend  # kun hvis backend bruger frontend-ting direkte
)

# CLI – link KUN top-lag; resten kommer transitivt
add_executable(lentzc cli/lentzc.cpp)
target_link_libraries(lentzc
        PRIVATE lentz_backend
        PRIVATE lentz_frontend   # hvis CLI også taler direkte til frontend
)