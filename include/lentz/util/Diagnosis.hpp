#pragma once
#include <iostream>
#include <string>

namespace lentz::util {
    inline void info(const std::string& m) { std::cerr << "[info] " << m << "\n"; }
    inline void warn(const std::string& m) { std::cerr << "[warn] " << m << "\n"; }
    inline void error(const std::string& m){ std::cerr << "[error] " << m << "\n"; }
} // namespace lentz::util
