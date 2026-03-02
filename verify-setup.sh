#!/bin/bash

# Quick script to verify your package setup and test local build
# Run this before creating a GitHub release

echo "====================================="
echo "🔍 Nepali Date - Pre-Publish Checklist"
echo "====================================="
echo ""

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: pom.xml not found. Run this from the project root."
    exit 1
fi

echo "✅ In correct directory"
echo ""

# Check Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven first."
    exit 1
fi

echo "✅ Maven is installed: $(mvn --version | head -n 1)"
echo ""

# Clean and build
echo "📦 Building project..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful"
echo ""

# Run tests
echo "🧪 Running tests..."
mvn test
if [ $? -ne 0 ]; then
    echo "❌ Tests failed!"
    exit 1
fi

echo "✅ All tests passed"
echo ""

# Check if sources and javadoc jars are generated
echo "📚 Verifying artifacts..."
if [ -f "target/nepali-date-1.0.0.jar" ]; then
    echo "✅ Main JAR created"
else
    echo "❌ Main JAR not found"
fi

if [ -f "target/nepali-date-1.0.0-sources.jar" ]; then
    echo "✅ Sources JAR created"
else
    echo "⚠️  Sources JAR not found (run: mvn source:jar)"
fi

if [ -f "target/nepali-date-1.0.0-javadoc.jar" ]; then
    echo "✅ Javadoc JAR created"
else
    echo "⚠️  Javadoc JAR not found (run: mvn javadoc:jar)"
fi

echo ""
echo "====================================="
echo "📋 Next Steps to Publish:"
echo "====================================="
echo ""
echo "1. Create a GitHub Release:"
echo "   → Go to: https://github.com/prabesh-suwal/nepali-date/releases/new"
echo "   → Select tag: v1.0.0"
echo "   → Title: v1.0.0 - Initial Release"
echo "   → Description: Add your release notes"
echo "   → Click 'Publish release'"
echo ""
echo "2. GitHub Actions will automatically:"
echo "   → Build the project"
echo "   → Run tests"
echo "   → Generate sources and javadoc JARs"
echo "   → Publish to GitHub Packages"
echo ""
echo "3. Monitor the workflow:"
echo "   → https://github.com/prabesh-suwal/nepali-date/actions"
echo ""
echo "4. After successful publish, your package will be at:"
echo "   → https://github.com/prabesh-suwal/nepali-date/packages"
echo ""
echo "====================================="
echo "✨ Setup Complete!"
echo "====================================="

