/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import javax.media.opengl.GL3;

/**
 *
 * @author paulovich
 */
public class Pipeline {

    private final Shader shader;
    private final Matrix4 modelMatrix;
    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    private final Matrix4 modelviewMatrix;
    private final Matrix4 normalMatrix;
    private final Light ambientLight;
    
    public enum LightNumber {

        LIGHT0
    };

    public enum MatrixType {

        MODEL, VIEW, PROJECTION
    };

    public Pipeline() {
        // Carrega os shaders
        shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.JWAVEFRONT_SHADER);
        modelMatrix = new Matrix4();        // 1- Mapeamento de objetos NO mundo
        viewMatrix = new Matrix4();         // 2- Para onde quero olhar dentro do mundo
        projectionMatrix = new Matrix4();   // 3- Projecao de todas as coord. 
        
        modelviewMatrix = new Matrix4();    // View * Model em CPU (p/ maior eficiencia na GPU)
        
        normalMatrix = new Matrix4();
        ambientLight = new Light();
    }

    public void init(GL3 gl) {
        //inicializa e ativa o shader
        shader.init(gl);
        shader.bind();

        //inicializa as matrizes ModelView e Projection
        projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
        modelviewMatrix.init(gl, shader.getUniformLocation("u_modelviewMatrix"));
        normalMatrix.init(gl, shader.getUniformLocation("u_normalMatrix"));
        viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));
                
        //inicializa a luz
        ambientLight.init(gl, shader);
    }

    public Matrix4 getMatrix(MatrixType type) {
        if (type == MatrixType.MODEL) {
            return this.modelMatrix;
        } else if (type == MatrixType.VIEW) {
            return this.viewMatrix;
        } else if (type == MatrixType.PROJECTION) {
            return this.projectionMatrix;
        }
        return null;
    }

    public Shader getShader() {
        return this.shader;
    }

    public Light getLight(LightNumber number) {
        if (number == LightNumber.LIGHT0) {
            return this.ambientLight;
        }
        return null;
    }

    public void bind() {
        this.projectionMatrix.bind();
        this.modelviewMatrix.copy(viewMatrix).multiply(modelMatrix).bind();
        this.normalMatrix.copy(modelviewMatrix).bind();
        this.viewMatrix.bind();
        this.ambientLight.bind();
    }

}
