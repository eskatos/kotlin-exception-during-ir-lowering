abstract class MyInlinePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("run") {
            doLast { println("It runs!") }
        }
    }
}

apply<MyInlinePlugin>()

