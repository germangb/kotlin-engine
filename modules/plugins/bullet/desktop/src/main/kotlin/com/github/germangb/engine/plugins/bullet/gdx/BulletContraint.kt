package com.github.germangb.engine.plugins.bullet.gdx

import com.badlogic.gdx.physics.bullet.dynamics.btTypedConstraint
import com.github.germangb.engine.plugin.bullet.PhysicsContraint

/** Holds bullet constraint */
class BulletContraint(val btConstraint: btTypedConstraint) : PhysicsContraint