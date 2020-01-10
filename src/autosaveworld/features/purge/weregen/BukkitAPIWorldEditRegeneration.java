/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package autosaveworld.features.purge.weregen;

import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.World;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;

import autosaveworld.core.logging.MessageLogger;
import autosaveworld.features.purge.weregen.UtilClasses.BlockToPlaceBack;
import autosaveworld.features.purge.weregen.UtilClasses.ItemSpawnListener;
import autosaveworld.features.purge.weregen.WorldEditRegeneration.WorldEditRegenrationInterface;
import autosaveworld.utils.BukkitUtils;

//TODO: Migrate to new WorldEdit API
public class BukkitAPIWorldEditRegeneration implements WorldEditRegenrationInterface {

	private ItemSpawnListener itemremover = new ItemSpawnListener();

	@Override
	public void regenerateRegion(World world, BlockVector3 minpoint, BlockVector3 maxpoint) {
		BukkitWorld bw = new BukkitWorld(world);
		EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), Integer.MAX_VALUE);
		es.setFastMode(true);
		int maxy = bw.getMaxY() + 1;
		Region region = new CuboidRegion(bw, minpoint, maxpoint);
		LinkedList<BlockToPlaceBack> placeBackQueue = new LinkedList<BlockToPlaceBack>();

		// register listener that will prevent trash items from spawning
		BukkitUtils.registerListener(itemremover);

		// first save all blocks that are inside affected chunks but outside the region
		for (BlockVector2 chunk : region.getChunks()) {
			BlockVector3 min = chunk.toBlockVector3();
			for (int x = 0; x < 16; ++x) {
				for (int y = 0; y < maxy; ++y) {
					for (int z = 0; z < 16; ++z) {
						BlockVector3 pt = min.add(x, y, z);
						if (!region.contains(pt)) {
							placeBackQueue.add(new BlockToPlaceBack(pt.toVector3(), es.getBlock(pt).toBaseBlock()));
						}
					}
				}
			}
		}

		//TODO: Set blocks that has tileentity to air first

		// regenerate all affected chunks
		for (BlockVector2 chunk : region.getChunks()) {
			try {
				world.regenerateChunk(chunk.getBlockX(), chunk.getBlockZ());
			} catch (Exception t) {
				MessageLogger.exception("Unable to regenerate chunk " + chunk.getBlockX() + " " + chunk.getBlockZ(), t);
			}
		}

		// set all blocks that were outside the region back
		for (PlaceBackStage stage : placeBackStages) {
			stage.processBlockPlaceBack(world, es, placeBackQueue);
		}

		// unregister listener that prevents item drop
		BukkitUtils.unregisterListener(itemremover);
	}

	private static PlaceBackStage[] placeBackStages = new PlaceBackStage[] {
		// normal stage place back
		new PlaceBackStage(new PlaceBackStage.PlaceBackCheck() {
			@Override
			public boolean shouldPlaceBack(BaseBlock block) {
				return !PlacementOrder.shouldPlaceLast(BukkitAdapter.adapt(block.getBlockType())) && !PlacementOrder.shouldPlaceFinal(BukkitAdapter.adapt(block.getBlockType()));
			}
		}),
		// last stage place back
		new PlaceBackStage(new PlaceBackStage.PlaceBackCheck() {
			@Override
			public boolean shouldPlaceBack(BaseBlock block) {
				return PlacementOrder.shouldPlaceLast(BukkitAdapter.adapt(block.getBlockType()));
			}
		}),
		// final stage place back
		new PlaceBackStage(new PlaceBackStage.PlaceBackCheck() {
			@Override
			public boolean shouldPlaceBack(BaseBlock block) {
				return PlacementOrder.shouldPlaceFinal(BukkitAdapter.adapt(block.getBlockType()));
			}
		})
	};

	private static class PlaceBackStage {

		public static interface PlaceBackCheck {
			public boolean shouldPlaceBack(BaseBlock block);
		}

		private PlaceBackCheck check;

		public PlaceBackStage(PlaceBackCheck check) {
			this.check = check;
		}

		public void processBlockPlaceBack(World world, EditSession es, LinkedList<BlockToPlaceBack> placeBackQueue) {
			Iterator<BlockToPlaceBack> entryit = placeBackQueue.iterator();
			while (entryit.hasNext()) {
				BlockToPlaceBack blockToPlaceBack = entryit.next();
				BaseBlock block = blockToPlaceBack.getBlock();
				if (check.shouldPlaceBack(block)) {
					Vector3 pt = blockToPlaceBack.getPosition();
					try {
						// set block to air to fix one really weird problem
						world.getBlockAt((int) pt.getX(),(int) pt.getY(),(int) pt.getZ()).setType(Material.AIR);
						// set block back if it is not air
						if (block.getBlockType() == BlockTypes.AIR) {
							es.setBlock(pt.toBlockPoint(), block);
						}
					} catch (Exception t) {
						MessageLogger.exception("Unable to place back block " + pt.getX() + " " + pt.getY() + " " + pt.getZ(), t);
					} finally {
						entryit.remove();
					}
				}
			}
		}

	}

}
