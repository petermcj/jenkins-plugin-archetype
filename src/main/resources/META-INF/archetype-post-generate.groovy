import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Rename "gitignore" → ".gitignore".
// Maven archetype packaging strips leading-dot files from the JAR, so the
// file is shipped without the dot and restored here after generation.
Path projectDir = Paths.get(request.outputDirectory, request.artifactId)
Path src = projectDir.resolve("gitignore")
Path dst = projectDir.resolve(".gitignore")

if (Files.exists(src)) {
    Files.move(src, dst)
}
